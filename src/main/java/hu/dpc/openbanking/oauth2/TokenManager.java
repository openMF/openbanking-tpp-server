package hu.dpc.openbanking.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.controller.AISPController;
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
    private static final Logger LOG = LoggerFactory.getLogger(AISPController.class);


    private final OAuthConfig oauthconfig;

    public TokenManager(OAuthConfig config) {
        this.oauthconfig = config;
    }

    private static String getPostDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
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

    private static void debugToken(TokenResponse token) {
        System.out.println("HTTP: " + token.getHttpResponseCode());
        System.out.println("Access token: " + token.getAccessToken());
        System.out.println("Refresh token: " + token.getRefreshToken());
        System.out.println("Scope: " + token.getScope());
        System.out.println("ID Token: " + token.getIdToken());
        System.out.println("Token type: " + token.getTokenType());
        System.out.println("Expires in: " + token.getExpiresIn());
        System.out.println("Subject: " + token.getSubject());
        System.out.println("JWT expires: " + token.getJwtExpires());
        System.err.println("Error: " + token.getError());
        System.err.println("Error description: " + token.getErrorDescription());

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
    private TokenResponse doPost(HashMap<String, String> postDataParams) {
        int responseCode = -1;
        for (int trycount = HttpHelper.CONNECTION_REFUSED_TRYCOUNT; trycount-- > 0; ) {
            try {
                URL url = oauthconfig.getTokenURL();

                LOG.info("Call {}", url.toString());
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                HttpsTrust.INSTANCE.trust(conn);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String authorization = Base64.getEncoder().encodeToString((oauthconfig.getApiKey() + ':' + oauthconfig.getApiSecret()).getBytes());
                conn.setRequestProperty("Authorization", "Basic " + authorization);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                responseCode = conn.getResponseCode();
                LOG.info("Response code: {}", responseCode);
                String response = HttpHelper.getResponseContent(conn);
                LOG.info("Response: [{}]", response);

                if (response.startsWith("<")) {
                    // Response is not JSON
                    TokenResponse result = new TokenResponse();
                    result.setHttpResponseCode(responseCode);
                    result.setRawContent(response);
                    return result;
                }
                ObjectMapper mapper = new ObjectMapper();
                TokenResponse result = mapper.readValue(response, TokenResponse.class);
                result.setRawContent(response);
                result.setHttpResponseCode(responseCode);

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    if (null != result.getIdToken()) {
                        try {
                            DecodedJWT jwt = JWT.decode(result.getIdToken());
                            result.setSubject(jwt.getSubject());
                            result.setJwtExpires(jwt.getExpiresAt().getTime());
                        } catch (JWTDecodeException exception) {
                            //Invalid token
                        }
                    }
                }

                return result;
            } catch (ConnectException ce) {
                LOG.error("Connection refused: trying... {} {}", trycount, ce.getLocalizedMessage());
                if (trycount > 0) {
                    try {
                        Thread.sleep(HttpHelper.CONNECTION_REFUSED_WAIT_IN_MS);
                    } catch (InterruptedException e) {
                        // DO NOTHING
                    }
                } else {
                    throw new APICallException("Connection refused");
                }
            } catch (IOException e) {
                LOG.error("Something went wrong: ", e);
                throw new APICallException(e.getLocalizedMessage());
            }
        }

        TokenResponse result = new TokenResponse();
        result.setHttpResponseCode(responseCode);
        return result;
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
    public TokenResponse getAccessTokenWithClientCredential(String[] scopes) {
        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("grant_type", "client_credentials");
        postDataParams.put("scope", String.join(" ", scopes));

        return doPost(postDataParams);
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
    public TokenResponse getAccessTokenFromCode(String code) {
        HashMap<String, String> postDataParams = new HashMap<>();
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
    public TokenResponse refreshToken(String token) {
        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("client_id", oauthconfig.getApiKey());
        postDataParams.put("grant_type", "refresh_token");
        postDataParams.put("refresh_token", token);
        postDataParams.put("redirect_uri", oauthconfig.getCallbackURL());

        return doPost(postDataParams);
    }
}
