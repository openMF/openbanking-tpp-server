/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbanking.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
import hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2.TokenResponse;
import hu.dpc.openbank.tpp.acefintech.backend.repository.APICallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TokenManager {
    private static final Logger LOG = LoggerFactory.getLogger(TokenManager.class);


    private final OAuthConfig oauthconfig;

    public TokenManager(final OAuthConfig config) {
        oauthconfig = config;
    }

    /**
     * <pre>curl -k -X POST "https://localhost:8243/token"
     *           -H "Content-Type: application/x-www-form-urlencoded"
     *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"
     *           -d "grant_type=client_credentials"
     *           -d "scope=accounts openid"</pre>
     *
     * @return
     */
    public TokenResponse getAccessTokenWithClientCredential(final String[] scopes) {
        final HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("grant_type", "client_credentials");
        postDataParams.put("scope", String.join(" ", scopes));

        return doPost(postDataParams);
    }

    public OAuthConfig getOauthconfig() {
        return oauthconfig;
    }

    /**
     * Execute token POST request.
     *
     * @param postDataParams required params.k
     * @return
     */
    private TokenResponse doPost(final Map<String, String> postDataParams) {
        int responseCode = -1;
        for (int trycount = HttpHelper.CONNECTION_REFUSED_TRYCOUNT; 0 < trycount--; ) {
            try {
                final URL url = oauthconfig.getTokenURL();

                LOG.info("Call {}", url);
                final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                HttpsTrust.INSTANCE.trust(conn);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Accept", WSO2Controller.APPLICATION_JSON);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                final String authorization = Base64.getEncoder().encodeToString((oauthconfig.getApiKey() + ':' + oauthconfig.getApiSecret()).getBytes());
                conn.setRequestProperty("Authorization", "Basic " + authorization);

                final OutputStream os = conn.getOutputStream();
                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                responseCode = conn.getResponseCode();
                LOG.info("Response code: {}", responseCode);
                final String response = HttpHelper.getResponseContent(conn);
                LOG.info("Response: [{}]", response);

                if (!response.isEmpty() && '<' == response.charAt(0)) {
                    // Response is not JSON
                    final TokenResponse result = new TokenResponse();
                    result.setHttpResponseCode(responseCode);
                    result.setRawContent(response);
                    return result;
                }
                final ObjectMapper mapper = new ObjectMapper();
                final TokenResponse result = mapper.readValue(response, TokenResponse.class);
                result.setRawContent(response);
                result.setHttpResponseCode(responseCode);

                if (java.net.HttpURLConnection.HTTP_OK == responseCode) {
                    if (null != result.getIdToken()) {
                        try {
                            final DecodedJWT jwt = JWT.decode(result.getIdToken());
                            result.setSubject(jwt.getSubject());
                            result.setJwtExpires(jwt.getExpiresAt().getTime());
                        } catch (final JWTDecodeException exception) {
                            //Invalid token
                        }
                    }
                }

                return result;
            } catch (final ConnectException ce) {
                LOG.error("Connection refused: trying... {} {}", trycount, ce.getLocalizedMessage());
                if (0 < trycount) {
                    try {
                        Thread.sleep(HttpHelper.CONNECTION_REFUSED_WAIT_IN_MS);
                    } catch (final InterruptedException e) {
                        // DO NOTHING
                    }
                } else {
                    throw new APICallException("Connection refused");
                }
            } catch (final IOException e) {
                LOG.error("Something went wrong: ", e);
                throw new APICallException(e.getLocalizedMessage());
            }
        }

        final TokenResponse result = new TokenResponse();
        result.setHttpResponseCode(responseCode);
        return result;
    }

    private static String getPostDataString(final Map<String, String> params) {
        final StringBuilder result = new StringBuilder(4096);
        boolean first = true;
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return result.toString();
    }

    /**
     * <pre>curl -k -v -X POST "https://localhost:8243/token?
     *                               code=cc0970dd-476a-381e-bdd3-84bf69091932
     *                              &grant_type=authorization_code
     *                              &redirect_uri=http://acefintech.org/callback
     *                              &client_id=nYdJa_KHnicVXCYEMNSgKVCiCzwa"
     *           -H "Content-Type: application/x-www-form-urlencoded"
     *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"</pre>
     *
     * @param code
     * @return
     */
    public TokenResponse getAccessTokenFromCode(final String code) {
        final HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("code", code);
        postDataParams.put("client_id", oauthconfig.getApiKey());
        postDataParams.put("grant_type", "authorization_code");
        postDataParams.put("redirect_uri", oauthconfig.getCallbackURL());

        return doPost(postDataParams);
    }

    /**
     * <pre>curl -k -v -X POST "https://localhost:8243/token?
     *                  refresh_token=e5fd4066-d3de-33cb-a5e5-3b50c36225ec
     *                 &grant_type=refresh_token
     *                 &redirect_uri=http://acefintech.org/callback
     *                 &client_id=nYdJa_KHnicVXCYEMNSgKVCiCzwa"
     *           -H "Content-Type: application/x-www-form-urlencoded"
     *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"</pre>
     *
     * @param token
     * @return
     */
    public TokenResponse refreshToken(final String token) {
        final HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("client_id", oauthconfig.getApiKey());
        postDataParams.put("grant_type", "refresh_token");
        postDataParams.put("refresh_token", token);
        postDataParams.put("redirect_uri", oauthconfig.getCallbackURL());

        return doPost(postDataParams);
    }
}
