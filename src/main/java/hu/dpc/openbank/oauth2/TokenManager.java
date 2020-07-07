/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.common.http.HttpHelper;
import hu.dpc.common.http.HttpsTrust;
import hu.dpc.common.http.oauth2.TokenResponse;
import hu.dpc.openbank.exceptions.APICallException;
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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

@Slf4j
public class TokenManager {

  private static final String OPENBANKING_SCOPES = "openid profile accounts payments";
  private static final String GRANT_TYPE = "grant_type";
  private static final String SCOPE = "scope";
  private static final String CLIENT_CREDENTIALS = "client_credentials";
  private static final String ACCEPT = "Accept";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
  private static final String POST = "POST";
  private static final String AUTHORIZATION = "Authorization";
  private static final String REFRESH_TOKEN = "refresh_token";
  private static final String AUTHORIZATION_CODE = "authorization_code";
  private static final String CLIENT_ID = "client_id";
  private static final String CODE = "code";
  private static final String REDIRECT_URI = "redirect_uri";
  @Nonnull
  private final OAuthConfig oauthconfig;


  public TokenManager(final OAuthConfig config) {
    oauthconfig = config;
  }


  /**
   * Convert value-key pairs to urlencoded representation for POST
   */
  @Nonnull
  private static String getPostDataString(final Map<String, String> params) {
    if (params.isEmpty()) {
      return "";
    }
    final StringBuilder result = new StringBuilder(4096);
    for (final Map.Entry<String, String> entry : params.entrySet()) {
      result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)) //
          .append('=').append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)) //
          .append('&');
    }
    // remove latest '&' char from end
    result.setLength(result.length() - 1);

    return result.toString();
  }


  /**
   * <pre>curl -k -X POST "https://localhost:8243/token"
   *           -H "Content-Type: application/x-www-form-urlencoded"
   *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"
   *           -d "grant_type=client_credentials"
   *           -d "scope=accounts openid"</pre>
   */
  @Nonnull
  public TokenResponse getAccessTokenWithClientCredential() {
    final HashMap<String, String> postDataParams = new HashMap<>();
    postDataParams.put(GRANT_TYPE, CLIENT_CREDENTIALS);
    postDataParams.put(SCOPE, OPENBANKING_SCOPES);

    return doPost(postDataParams);
  }


  @Nonnull
  public OAuthConfig getOauthconfig() {
    return oauthconfig;
  }


  /**
   * Execute token POST request.
   *
   * @param postDataParams required params.k
   */
  private TokenResponse doPost(final Map<String, String> postDataParams) {
    int responseCode = -1;
    for (int trycount = HttpHelper.CONNECTION_REFUSED_TRYCOUNT; trycount != 0; trycount--) {
      try {
        final URL url = oauthconfig.getTokenURL();

        log.info("Call {}", url);
        final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        HttpsTrust.INSTANCE.trust(conn);
        conn.setReadTimeout(15_000);
        conn.setConnectTimeout(15_000);
        conn.setRequestMethod(POST);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestProperty(ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        conn.setRequestProperty(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);

        final String authorization = Base64.getEncoder()
            .encodeToString((oauthconfig.getApiKey() + ':' + oauthconfig.getApiSecret()).getBytes());
        conn.setRequestProperty(AUTHORIZATION, "Basic " + authorization);

        try (final OutputStream os = conn
            .getOutputStream(); final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
          writer.write(getPostDataString(postDataParams));
          writer.flush();
        }

        responseCode = conn.getResponseCode();
        log.info("Response code: {}", responseCode);
        final String response = HttpHelper.getResponseContent(conn);
        log.info("Response: [{}]", response);

        if (!response.isEmpty() && '<' == response.charAt(0)) {
          // Response is not JSON
          final TokenResponse result = new TokenResponse();
          result.setHttpResponseCode(responseCode);
          result.setHttpRawContent(response);
          return result;
        }
        final ObjectMapper mapper = new ObjectMapper();
        final TokenResponse result = mapper.readValue(response, TokenResponse.class);
        result.setHttpRawContent(response);
        result.setHttpResponseCode(responseCode);

        if (java.net.HttpURLConnection.HTTP_OK == responseCode && null != result.getIdToken()) {
          decodeJWT(result).ifPresent(jwt -> {
            result.setSubject(jwt.getSubject());
            result.setJwtExpires(jwt.getExpiresAt().getTime());
          });
        }

        return result;
      } catch (final ConnectException ce) {
        log.error("Connection refused: trying... {} {}", trycount, ce.getLocalizedMessage());
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
        log.error("Something went wrong: ", e);
        throw new APICallException(e.getLocalizedMessage());
      }
    }

    final TokenResponse result = new TokenResponse();
    result.setHttpResponseCode(responseCode);
    return result;
  }


  @Nonnull
  private Optional<DecodedJWT> decodeJWT(final TokenResponse result) {
    try {
      return Optional.of(JWT.decode(result.getIdToken()));
    } catch (final JWTDecodeException exception) {
      //Invalid token
    }
    return Optional.empty();
  }


  /**
   * <pre>curl -k -v -X POST "https://localhost:8243/token?
   *                               code=cc0970dd-476a-381e-bdd3-84bf69091932
   *                              &grant_type=authorization_code
   *                              &redirect_uri=http://acefintech.org/callback
   *                              &client_id=nYdJa_KHnicVXCYEMNSgKVCiCzwa"
   *           -H "Content-Type: application/x-www-form-urlencoded"
   *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"</pre>
   */
  @Nonnull
  public TokenResponse getAccessTokenFromCode(final String code) {
    final HashMap<String, String> postDataParams = createPostParams(AUTHORIZATION_CODE);
    postDataParams.put(CODE, code);

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
   */
  @Nonnull
  public TokenResponse refreshToken(final String token) {
    final HashMap<String, String> postDataParams = createPostParams(REFRESH_TOKEN);
    postDataParams.put(REFRESH_TOKEN, token);

    return doPost(postDataParams);
  }


  private HashMap<String, String> createPostParams(final String grantValue) {
    final HashMap<String, String> postDataParams = new HashMap<>();

    postDataParams.put(CLIENT_ID, oauthconfig.getApiKey());
    postDataParams.put(REDIRECT_URI, oauthconfig.getCallbackURL());
    postDataParams.put(GRANT_TYPE, grantValue);

    return postDataParams;
  }
}
