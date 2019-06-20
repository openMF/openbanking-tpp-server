package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.openbank.tpp.acefintech.backend.enity.HttpResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.AccessToken;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2.TokenResponse;
import hu.dpc.openbank.tpp.acefintech.backend.repository.*;
import hu.dpc.openbanking.oauth2.HttpHelper;
import hu.dpc.openbanking.oauth2.HttpsTrust;
import hu.dpc.openbanking.oauth2.OAuthConfig;
import hu.dpc.openbanking.oauth2.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityNotFoundException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WSO2Controller {

    private static final String SCOPE_ACCOUNTS = "accounts";
    private static final Logger LOG = LoggerFactory.getLogger(WSO2Controller.class);
    private static final HashMap<String, TokenManager> tokenManagerCache = new HashMap<>();
    private static final HashMap<String, AccessToken> clientAccessTokenCache = new HashMap<>();
    /**
     * WSO2 error message, when BANK API not available.
     */
    private static final String BANK_NOT_WORKING_ERROR = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>101503</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Error connecting to the back end</am:description></am:fault>";
    //    "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>303001</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Currently , Address endpoint : [ Name : AccountInformationAPI--vv1_APIproductionEndpoint ] [ State : SUSPENDED ]</am:description></am:fault>";
    private static final String BANK_APIS_SUSPENDED = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>303001</am:code>";
    private static final String WSO2_METHOD_NOT_FOUND = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>404</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>No matching resource found for given API Request</am:description></am:fault>";
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    /**
     * Getting bank infomations.
     */
    @Autowired
    private BankRepository bankRepository;

    /**
     * Get latest user AccessToken.
     *
     * @param userId
     * @param bankId
     * @return
     */
    private AccessToken getLatestUserAccessToken(String userId, String bankId) {
        LOG.info("getLatestUserAccessToken userId {} bankId {}", userId, bankId);
        AccessToken accessToken = accessTokenRepository.getLatest(bankId, userId, SCOPE_ACCOUNTS);
        LOG.info("AccessToken: {}", accessToken);
        return accessToken;
    }

    /**
     * TokenManager beszerzése és cachelése
     *
     * @param bankId
     * @return
     * @throws BankIDNotFoundException
     * @throws BankConfigException
     */
    public TokenManager getTokenManager(String bankId) {
        LOG.info("BankID: {}", bankId);

        TokenManager tokenManager = tokenManagerCache.get(bankId);
        if (null == tokenManager) {
            try {
                BankInfo bankInfo = bankRepository.getOne(bankId);
                if (null == bankInfo || !bankId.equals(bankInfo.getBankId())) {
                    // Testing result
                    throw new BankIDNotFoundException(bankId);
                }
                OAuthConfig oAuthConfig = new OAuthConfig(bankInfo);
                tokenManager = new TokenManager(oAuthConfig);
                tokenManagerCache.put(bankId, tokenManager);
            } catch (EntityNotFoundException e) {
                // BankId not found
                LOG.error("Bank ID not found! [{}]", bankId);
                throw new BankIDNotFoundException(bankId);
            } catch (MalformedURLException e) {
                LOG.error("Bank config error!", e);
                throw new BankConfigException(e.getLocalizedMessage());
            }
        }
        return tokenManager;
    }


    /**
     * Get or create client AccessToken
     *
     * @param bankId
     * @return
     */
    private String getClientAccessToken(String bankId, boolean force) {
        LOG.info("getClientAccessToken: bankId: {} force: {}", bankId, force);
        AccessToken accessToken = clientAccessTokenCache.get(bankId);
        LOG.info("Access token {} found in cache for bank {}", (accessToken == null ? "not" : ""), bankId);
        if (null != accessToken) {
            LOG.info("Cached Access token {} is expired {} expires {} current {}", accessToken.getAccessToken(), accessToken.isExpired(), accessToken.getExpires(), System.currentTimeMillis());
        }

        if (force || null == accessToken || accessToken.isExpired()) {
            TokenManager tokenManager = getTokenManager(bankId);
            TokenResponse tokenResponse = tokenManager.getAccessTokenWithClientCredential(new String[]{SCOPE_ACCOUNTS});
            int respondeCode = tokenResponse.getHttpResponseCode();
            if (respondeCode >= 200 && respondeCode < 300) {

                String accessTokenStr = tokenResponse.getAccessToken();
                LOG.debug("Client AccessToken: {}", accessTokenStr);

                // Save accessToken for later usage
                accessToken = new AccessToken();
                accessToken.setAccessToken(accessTokenStr);
                accessToken.setAccessTokenType("client");
                accessToken.setScope(SCOPE_ACCOUNTS);
                accessToken.setExpires(tokenResponse.getExpiresIn());
                accessToken.setBankId(bankId);

                LOG.info("New Access token {} is expired {} expires {} current {}", accessToken.getAccessToken(), accessToken.isExpired(), accessToken.getExpires(), System.currentTimeMillis());

                clientAccessTokenCache.put(bankId, accessToken);
            } else {
                throw new APICallException(tokenResponse.getRawContent());
            }
        }

        return accessToken.getAccessToken();
    }

    /**
     * Handle WSO2 errors
     *
     * @param content
     */
    public void checkWSO2Errors(String content) {
        if (null != content && content.startsWith("<")) {
            LOG.error("Respond in XML, it's mean something error occured! {}", content);
            if (BANK_NOT_WORKING_ERROR.equals(content)) {
                throw new APICallException("API gateway cannot connect to BANK backend!");
            }
            if (content.startsWith(BANK_APIS_SUSPENDED)) {
                throw new APICallException("API gateway suspended!");
            }
            if (WSO2_METHOD_NOT_FOUND.equals(content)) {
                throw new APICallException("API method not found!");
            }
            throw new APICallException("API gateway problem!");
        }
    }


    /**
     * Get Accounts ConsentId
     *
     * @param bankId
     * @return consentId if request it was not success return empty.
     */
    public String getConsentId(String bankId) {
        int tryCount = 3;
        boolean force = false;

        try {
            for (int ii = tryCount; ii-- > 0; ) {
                String accessToken = getClientAccessToken(bankId, force);
                BankInfo bankInfo = getTokenManager(bankId).getOauthconfig().getBankInfo();
                // Setup HTTP headers
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());
                // get ConsentID
                HttpResponse httpResponse = doAPICall(true, new URL(bankInfo.getAccountsUrl() + "/account-initiations"), headers, "{'Data':'valami'}");

                // Sometimes WSO2 respond errors in xml
                String content = httpResponse.getContent().trim();
                checkWSO2Errors(content);
                int respondCode = httpResponse.getResponseCode();
                if (respondCode >= 200 && respondCode < 300) {
                    // TODO Parse response & return consentId
                    LOG.error("Respond code {}; respond: [{}]", respondCode, content);
                    return "ConsentId:12345";
                }
                force = true;
            }

            throw new APICallException("ConsentID request fails!");
        } catch (MalformedURLException mue) {
            LOG.error("URL problems!", mue);
            throw new BankConfigException(mue.getLocalizedMessage());
        }
    }


    /**
     * Check user AccessToken is valid and not expired.
     *
     * @param bankId
     * @param userName
     * @throws OAuthAuthorizationRequiredException
     */
    public String userAccessTokenIsValid(String bankId, String userName) {
        LOG.info("userAccessTokenIsValid: bankId {} userName {}", bankId, userName);
        AccessToken userAccessToken = getLatestUserAccessToken(userName, bankId);
        if (null == userAccessToken) {
            String consentId = getConsentId(bankId);
            LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
            throw new OAuthAuthorizationRequiredException(consentId);
        }

        TokenManager tokenManager = getTokenManager(bankId);
        if (userAccessToken.isExpired()) {
            LOG.info("User AccessToken is expired, trying refresh accessToken: [{}] refreshToken: [{}]", userAccessToken.getAccessToken(), userAccessToken.getRefreshToken());
            TokenResponse refreshToken = tokenManager.refreshToken(userAccessToken.getRefreshToken());

            if (HttpsURLConnection.HTTP_OK == refreshToken.getHttpResponseCode()) {
                userAccessToken = createAndSaveUserAccessToken(refreshToken, bankId, userName);
            } else {
                LOG.warn("Refresh token refreshing not succeeded. HTTP[{}] RAWResponse [{}]", refreshToken.getHttpResponseCode(), refreshToken.getRawContent());
                String consentId = getConsentId(bankId);
                LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
                throw new OAuthAuthorizationRequiredException(consentId);
            }
        }

        return userAccessToken.getAccessToken();
    }


    /**
     * Execute API request.
     *
     * @param post            request is POST
     * @param headerParams
     * @param jsonContentData
     * @return
     */
    public HttpResponse doAPICall(boolean post, URL url, Map<String, String> headerParams, String jsonContentData) {
        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            HttpsTrust.INSTANCE.trust(conn);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(post ? "POST" : "GET");
            conn.setDoInput(true);
            conn.setDoOutput(post);

            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (post) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

                writer.write(jsonContentData);

                writer.flush();
                writer.close();
                os.close();
            }

            int responseCode = conn.getResponseCode();
            LOG.info("Response code: {}", responseCode);
            String response = HttpHelper.getResponseContent(conn);
            LOG.info("Response: [{}]", response);

            httpResponse.setResponseCode(responseCode);
            httpResponse.setContent(response);
            return httpResponse;
        } catch (IOException e) {
            httpResponse.setResponseCode(-1);
            httpResponse.setContent(e.getLocalizedMessage());
            LOG.error("doAPICall", e);
        }

        return httpResponse;
    }

    /**
     * Create and Save user AccessToken from TokenResponse (code exchange/refreshToken)
     *
     * @param refreshToken
     * @param bankId
     * @param userName
     * @return
     */
    public AccessToken createAndSaveUserAccessToken(TokenResponse refreshToken, String bankId, String userName) {
        AccessToken userAccessToken = new AccessToken();
        userAccessToken.setAccessToken(refreshToken.getAccessToken());
        userAccessToken.setAccessTokenType("user");
        userAccessToken.setScope(SCOPE_ACCOUNTS);
        userAccessToken.setExpires(refreshToken.getJwtExpires());
        userAccessToken.setRefreshToken(refreshToken.getRefreshToken());
        userAccessToken.setBankId(bankId);
        userAccessToken.setUserName(userName);
        accessTokenRepository.save(userAccessToken);

        return userAccessToken;
    }

}
