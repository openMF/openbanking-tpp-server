package hu.dpc.openbanking.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2.TokenResponse;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TokenManager {
    static String CLIENT_ID = "nYdJa_KHnicVXCYEMNSgKVCiCzwa";
    static String CLIENT_SECRET = "GZhW8YCfC8TC4u2GaRXSXlcRNGwa";

    private OAuthConfig config;

    public TokenManager(OAuthConfig config) {
        this.config = config;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
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

    public static void doit() throws MalformedURLException {
        OAuthConfig oauthConfig = new OAuthConfig();
        oauthConfig.setApiKey(CLIENT_ID);
        oauthConfig.setApiSecret(CLIENT_SECRET);
        oauthConfig.setTokenURL("https://localhost:8243/token");
        oauthConfig.setSubject("acefintech");
        oauthConfig.setCallbackURL("http://acefintech.org/callback");

        TokenManager tokenManager = new TokenManager(oauthConfig);

        String[] scopes = new String[]{"accounts", "openid"};
        Arrays.sort(scopes);
        TokenResponse token = tokenManager.getAccessTokenWithClientCredential(scopes);
        debugToken(token);
        if (token.getHttpResponseCode() == HttpsURLConnection.HTTP_OK) {
            try {
                DecodedJWT jwt = JWT.decode(token.getIdToken());
                String subject = jwt.getSubject();
                if (null != subject && !oauthConfig.getSubject().equals(subject)) {
                    System.out.println("Subject not equals");
                }
            } catch (JWTDecodeException exception) {
                //Invalid token
            }
        }

        String[] tokenScopes = token.getScope().split(" ");
        Arrays.sort(tokenScopes);

        for (String scope : scopes) {
            boolean requestedScopeFound = false;
            for (String tokenScope : tokenScopes) {
                if (scope.equals(tokenScope)) {
                    requestedScopeFound = true;
                    break;
                }
            }
            if (!requestedScopeFound) {
                System.err.println("Requested scope not found! [" + scope + "]");
            }
        }

        TokenResponse userLevelAccessToken = tokenManager.getAccessTokenFromCode("83710e06-7e03-355d-8dd3-937ab320bdc8");
        debugToken(userLevelAccessToken);
        TokenResponse refreshToken = tokenManager.refreshToken("83710e06-7e03-355d-8dd3-937ab320bdc8");
        debugToken(refreshToken);
    }

    private static void debugToken(TokenResponse token) {
        System.out.println("HTTP: " + token.getHttpResponseCode());
        System.out.println("Access token: " + token.getAccessToken());
        System.out.println("Refresh token: " + token.getRefreshToken());
        System.out.println("Scope: " + token.getScope());
        System.out.println("ID Token: " + token.getIdToken());
        System.out.println("Token type: " + token.getTokenType());
        System.out.println("Expires in: " + token.getExpiresIn());
        System.err.println("Error: " + token.getError());
        System.err.println("Error description: " + token.getErrorDescription());

    }

    /**
     * Execute token POST request.
     * @param postDataParams required params.k
     * @return
     */
    public TokenResponse doPost(HashMap<String, String> postDataParams) {
        int responseCode = -1;
        try {
            URL url = config.getTokenURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            HttpsTrust.INSTANCE.trust(conn);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String authorization = Base64.getEncoder().encodeToString((config.getApiKey() + ':' + config.getApiSecret()).getBytes());
            conn.setRequestProperty("Authorization", "Basic " + authorization);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            responseCode = conn.getResponseCode();

            ObjectMapper mapper = new ObjectMapper();
            String response = "";
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader((responseCode == HttpsURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
            conn.disconnect();

            TokenResponse result = mapper.readValue(response, TokenResponse.class);
            result.setHttpResponseCode(responseCode);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        TokenResponse result = new TokenResponse();
        result.setHttpResponseCode(responseCode);
        return result;
    }

    /**
     * <pre>curl -k -X doPost "https://localhost:8243/token"
     *           -H "Content-Type: application/x-www-form-urlencoded"
     *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"
     *           -d "grant_type=client_credentials"
     *           -d "scope=accounts openid"</pre>
     *
     * @return
     */
    public TokenResponse getAccessTokenWithClientCredential(String[] scopes) {
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        postDataParams.put("grant_type", "client_credentials");
        postDataParams.put("scope", String.join(" ", scopes));

        return doPost(postDataParams);
    }

    /**
     * <pre>curl -k -v -X doPost "https://localhost:8243/token?
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
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        postDataParams.put("code", code);
        postDataParams.put("client_id", config.getApiKey());
        postDataParams.put("grant_type", "authorization_code");
        postDataParams.put("redirect_uri", config.getCallbackURL());

        return doPost(postDataParams);
    }

    /**
     * <pre>curl -k -v -X doPost "https://localhost:8243/token?
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
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        postDataParams.put("client_id", config.getApiKey());
        postDataParams.put("grant_type", "refresh_token");
        postDataParams.put("refresh_token", token);
        postDataParams.put("redirect_uri", config.getCallbackURL());

        return doPost(postDataParams);
    }
}
