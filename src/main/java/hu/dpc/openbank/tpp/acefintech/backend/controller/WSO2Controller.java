/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.enity.HttpResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountConsentPermissions;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.Consents;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.ConsentsRequest;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.ConsentsResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.AccessToken;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2.TokenResponse;
import hu.dpc.openbank.tpp.acefintech.backend.repository.*;
import hu.dpc.openbanking.oauth2.HttpHelper;
import hu.dpc.openbanking.oauth2.HttpsTrust;
import hu.dpc.openbanking.oauth2.OAuthConfig;
import hu.dpc.openbanking.oauth2.TokenManager;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityNotFoundException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ParametersAreNonnullByDefault
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
    @NonNls
    public static final String X_TPP_BANKID = "x-tpp-bankid";
    @NonNls
    public static final String ACCOUNT_ID = "AccountId";
    @NonNls
    public static final String APPLICATION_JSON = "application/json";
    @NonNls
    public static final String CONSENT_ID = "ConsentId";
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    /**
     * Getting bank infomations.
     */
    @Autowired
    private BankRepository bankRepository;

    /**
     * Execute API request.
     *
     * @param httpMethod
     * @param headerParams
     * @param jsonContentData
     * @return
     */
    public static HttpResponse doAPICall(final WSO2Controller.HTTP_METHOD httpMethod, final URL url, final Map<String, String> headerParams, @Nullable final String jsonContentData) {
        final HttpResponse httpResponse = new HttpResponse();
        for (int trycount = HttpHelper.CONNECTION_REFUSED_TRYCOUNT; 0 < trycount--; ) {
            try {
                LOG.info("doAPICall: {} {}", httpMethod.name(), url);
                final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                HttpsTrust.INSTANCE.trust(conn);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(httpMethod.name());
                conn.setDoInput(true);
                final boolean hasContent = !(null == jsonContentData && jsonContentData.isEmpty());

                conn.setDoOutput(hasContent);

                conn.setRequestProperty("Accept", APPLICATION_JSON);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                for (final Map.Entry<String, String> entry : headerParams.entrySet()) {
                    LOG.info("doAPICall-Header: {}: {}", entry.getKey(), entry.getValue());
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }

                if (hasContent) {
                    LOG.info("doAPICall-body: [{}]", jsonContentData);
                    final OutputStream os = conn.getOutputStream();
                    final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

                    writer.write(jsonContentData);

                    writer.flush();
                    writer.close();
                    os.close();
                }

                final int responseCode = conn.getResponseCode();
                LOG.info("Response code: {}", responseCode);
                final String response = HttpHelper.getResponseContent(conn);
                LOG.info("Response: [{}]\n[{}]", response, conn.getResponseMessage());

                httpResponse.setResponseCode(responseCode);
                httpResponse.setContent(response);
                return httpResponse;
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
                httpResponse.setResponseCode(-1);
                httpResponse.setContent(e.getLocalizedMessage());
                LOG.error("doAPICall", e);
                return httpResponse;
            }
        }

        return httpResponse;
    }

    /**
     * Handle WSO2 errors
     *
     * @param content
     */
    public static void checkWSO2Errors(final String content) {
        if (!content.isEmpty() && '<' == content.charAt(0)) {
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
     * Check user AccessToken is valid and not expired.
     *
     * @param bankId
     * @param userName
     * @throws OAuthAuthorizationRequiredException
     */
    public String userAccessTokenIsValid(final String bankId, final String userName) {
        LOG.info("userAccessTokenIsValid: bankId {} userName {}", bankId, userName);
        AccessToken userAccessToken = getLatestUserAccessToken(userName, bankId);
        if (null == userAccessToken) {
            final String consentId = getConsentId(bankId);
            LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
            throw new OAuthAuthorizationRequiredException(consentId);
        }

        final TokenManager tokenManager = getTokenManager(bankId);
        if (userAccessToken.isExpired()) {
            LOG.info("User AccessToken is expired, trying refresh accessToken: [{}] refreshToken: [{}]", userAccessToken.getAccessToken(), userAccessToken.getRefreshToken());
            final TokenResponse refreshToken = tokenManager.refreshToken(userAccessToken.getRefreshToken());

            if (HttpURLConnection.HTTP_OK == refreshToken.getHttpResponseCode()) {
                userAccessToken = createAndSaveUserAccessToken(refreshToken, bankId, userName);
            } else {
                LOG.warn("Refresh token refreshing not succeeded. HTTP[{}] RAWResponse [{}]", refreshToken.getHttpResponseCode(), refreshToken.getRawContent());
                final String consentId = getConsentId(bankId);
                LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
                throw new OAuthAuthorizationRequiredException(consentId);
            }
        }

        return userAccessToken.getAccessToken();
    }

    /**
     * Get latest user AccessToken.
     *
     * @param userId
     * @param bankId
     * @return
     */
    private AccessToken getLatestUserAccessToken(final String userId, final String bankId) {
        LOG.info("getLatestUserAccessToken userId {} bankId {}", userId, bankId);
        final AccessToken accessToken = accessTokenRepository.getLatest(bankId, userId, SCOPE_ACCOUNTS);
        LOG.info("AccessToken: {}", accessToken);
        return accessToken;
    }

    /**
     * Get Accounts ConsentId
     *
     * @param bankId
     * @return consentId if request it was not success return empty.
     */
    public String getConsentId(final String bankId) {
        final int tryCount = 3;
        boolean force = false;

        try {
            for (int ii = tryCount; 0 < ii--; ) {
                final String accessToken = getClientAccessToken(bankId, force);
                final BankInfo bankInfo = getTokenManager(bankId).getOauthconfig().getBankInfo();
                // Setup HTTP headers
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());
                final Consents consents = new Consents();
                consents.setPermissions(new ArrayList<>(AccountConsentPermissions.PERMISSIONS));
                LocalDateTime exp = LocalDateTime.now();
                exp = exp.plusYears(10);
                consents.setExpirationDateTime(exp);
                consents.setTransactionFromDateTime(LocalDateTime.now());
                consents.setTransactionToDateTime(exp);

                final ConsentsRequest consentsRequest = new ConsentsRequest(consents);

                final ObjectMapper mapper = new ObjectMapper();
                final String json;
                try {
                    json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(consentsRequest);
                    LOG.info("Consent request: {}", json);
                } catch (final JsonProcessingException e) {
                    throw new APICallException("Error creating JSON: " + e.getLocalizedMessage());
                }
                // get ConsentID
                final HttpResponse httpResponse = doAPICall(WSO2Controller.HTTP_METHOD.POST, new URL(bankInfo.getAccountsUrl() + "/account-access-consents"), headers, json);

                // Sometimes WSO2 respond errors in xml
                final String content = httpResponse.getContent();
                checkWSO2Errors(content);
                final int respondCode = httpResponse.getResponseCode();
                if (200 <= respondCode && 300 > respondCode) {
                    LOG.info("Respond code {}; respond: [{}]", respondCode, content);
                    final ConsentsResponse response = mapper.readValue(content, ConsentsResponse.class);
                    return response.getConsents().getConsentId();
                }
                LOG.error("Respond code {}; respond: [{}]", respondCode, content);
                force = true;
            }

            throw new APICallException("ConsentID request fails!");
        } catch (final MalformedURLException mue) {
            LOG.error("URL problems!", mue);
            throw new BankConfigException(mue.getLocalizedMessage());
        } catch (final Exception e) {
            LOG.error("Process error!", e);
            throw new BankConfigException(e.getLocalizedMessage());
        }
    }

    /**
     * TokenManager beszerzése és cachelése
     *
     * @param bankId
     * @return
     * @throws BankIDNotFoundException
     * @throws BankConfigException
     */
    public TokenManager getTokenManager(final String bankId) {
        LOG.info("BankID: {}", bankId);

        TokenManager tokenManager = tokenManagerCache.get(bankId);
        if (null == tokenManager) {
            try {
                final BankInfo bankInfo = bankRepository.getOne(bankId);
                if (null == bankInfo || !bankId.equals(bankInfo.getBankId())) {
                    // Testing result
                    throw new BankIDNotFoundException(bankId);
                }
                final OAuthConfig oAuthConfig = new OAuthConfig(bankInfo);
                tokenManager = new TokenManager(oAuthConfig);
                tokenManagerCache.put(bankId, tokenManager);
            } catch (final EntityNotFoundException e) {
                // BankId not found
                LOG.error("Bank ID not found! [{}]", bankId);
                throw new BankIDNotFoundException(bankId);
            } catch (final MalformedURLException e) {
                LOG.error("Bank config error!", e);
                throw new BankConfigException(e.getLocalizedMessage());
            }
        }
        return tokenManager;
    }

    /**
     * Create and Save user AccessToken from TokenResponse (code exchange/refreshToken)
     *
     * @param refreshToken
     * @param bankId
     * @param userName
     * @return
     */
    public AccessToken createAndSaveUserAccessToken(final TokenResponse refreshToken, final String bankId, final String userName) {
        final AccessToken userAccessToken = new AccessToken();
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

    /**
     * Get or create client AccessToken
     *
     * @param bankId
     * @return
     */
    protected String getClientAccessToken(final String bankId, final boolean force) {
        LOG.info("getClientAccessToken: bankId: {} force: {}", bankId, force);
        AccessToken accessToken = clientAccessTokenCache.get(bankId);
        LOG.info("Access token {} found in cache for bank {}", (null == accessToken ? "not" : ""), bankId);
        if (null != accessToken) {
            LOG.info("Cached Access token {} is expired {} expires {} current {}", accessToken.getAccessToken(), accessToken.isExpired(), accessToken.getExpires(), System.currentTimeMillis());
        }

        if (force || null == accessToken || accessToken.isExpired()) {
            final TokenManager tokenManager = getTokenManager(bankId);
            final TokenResponse tokenResponse = tokenManager.getAccessTokenWithClientCredential(new String[]{SCOPE_ACCOUNTS});
            final int respondeCode = tokenResponse.getHttpResponseCode();
            if (200 <= respondeCode && 300 > respondeCode) {

                final String accessTokenStr = tokenResponse.getAccessToken();
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

    protected ResponseEntity<String> handle(final WSO2Controller.HTTP_METHOD httpMethod, final String bankId, final User user, final String url, final String jsonContent) {
        LOG.info("BankID: {} User {}", bankId, user);
        try {
            final String userAccessToken = userAccessTokenIsValid(bankId, user.getUsername());
            final BankInfo bankInfo = getTokenManager(bankId).getOauthconfig().getBankInfo();

            // Setup HTTP headers
            final Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + userAccessToken);
            headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());
            final URL apiURL = new URL(bankInfo.getAccountsUrl() + url);
            LOG.info("Call API: {}", apiURL);
            final HttpResponse httpResponse = doAPICall(httpMethod, apiURL, headers, jsonContent);

            // Sometimes WSO2 respond errors in xml
            final String content = httpResponse.getContent();
            checkWSO2Errors(content);
            final int respondCode = httpResponse.getResponseCode();
            if (!(200 <= respondCode && 300 > respondCode)) {
                LOG.error("Respond code {}; respond: [{}]", respondCode, content);
            }
            final HttpStatus httpStatus = HttpStatus.resolve(respondCode);
            return new ResponseEntity<>(content, null == httpStatus ? HttpStatus.BAD_REQUEST : httpStatus);
        } catch (final OAuthAuthorizationRequiredException oare) {
            LOG.warn("Something went wrong!", oare);

            final HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("x-tpp-consentid", oare.getConsentId());
            return new ResponseEntity<>("Require authorize", responseHeaders, HttpStatus.PRECONDITION_REQUIRED);
        } catch (final Throwable e) {
            // Intended to catch Throwable
            LOG.error("Something went wrong!", e);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    public enum HTTP_METHOD {
        GET, POST, PUT, DELETE
    }

}
