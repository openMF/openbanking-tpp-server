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
import hu.dpc.common.http.HttpHelper;
import hu.dpc.common.http.HttpResponse;
import hu.dpc.common.http.oauth2.OAuthAuthorizationRequiredException;
import hu.dpc.common.http.oauth2.TokenResponse;
import hu.dpc.openbank.exceptions.APICallException;
import hu.dpc.openbank.oauth2.OAuthConfig;
import hu.dpc.openbank.oauth2.TokenManager;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountConsentPermissions;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.Consents;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.ConsentsRequest;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.ConsentsResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.AccessToken;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.repository.AccessTokenRepository;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankConfigException;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankIDNotFoundException;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankRepository;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityNotFoundException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class WSO2Controller {

    @NonNls
    public static final  String                        X_TPP_BANKID           = "x-tpp-bankid";
    @NonNls
    public static final  String                        ACCOUNT_ID             = "AccountId";
    @NonNls
    public static final  String                        CONSENT_ID             = "ConsentId";
    public static final  String                        SCOPE_ACCOUNTS         = "accounts";
    public static final  String                        SCOPE_PAYMENTS         = "payments";
    public static final  String                        X_FAPI_INTERACTION_ID  = "x-fapi-interaction-id";
    private static final Logger                        LOG                    = LoggerFactory
            .getLogger(WSO2Controller.class);
    private static final HashMap<String, TokenManager> tokenManagerCache      = new HashMap<>();
    private static final HashMap<String, AccessToken>  clientAccessTokenCache = new HashMap<>();
    @Autowired
    private              AccessTokenRepository         accessTokenRepository;
    /**
     * Getting bank infomations.
     */
    @Autowired
    private              BankRepository                bankRepository;


    /**
     * Check user AccessToken is valid and not expired.
     *
     * @param bankId
     * @param userName
     * @throws OAuthAuthorizationRequiredException
     */
    public String userAccessTokenIsValid(final String bankId, final String userName) {
        LOG.info("userAccessTokenIsValid: bankId {} userName {}", bankId, userName);
        AccessToken userAccessToken = accessTokenRepository.getLatest(bankId, userName);
        if (null == userAccessToken) {
            final String consentId = getAccountsConsentId(bankId);
            LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
            throw new OAuthAuthorizationRequiredException(consentId);
        }

        final TokenManager tokenManager = getTokenManager(bankId);
        if (userAccessToken.isExpired()) {
            LOG.info("User AccessToken is expired, trying refresh accessToken: [{}] refreshToken: [{}]", userAccessToken
                    .getAccessToken(), userAccessToken.getRefreshToken());
            final TokenResponse refreshToken = tokenManager.refreshToken(userAccessToken.getRefreshToken());

            if (HttpURLConnection.HTTP_OK == refreshToken.getHttpResponseCode()) {
                userAccessToken = createAndSaveUserAccessToken(refreshToken, bankId, userName);
            } else {
                LOG.warn("Refresh token refreshing not succeeded. HTTP[{}] RAWResponse [{}]", refreshToken
                        .getHttpResponseCode(), refreshToken.getHttpRawContent());
                final String consentId = getAccountsConsentId(bankId);
                LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
                throw new OAuthAuthorizationRequiredException(consentId);
            }
        }

        return userAccessToken.getAccessToken();
    }


    /**
     * Get Accounts ConsentId
     *
     * @param bankId
     * @return consentId if request it was not success return empty.
     */
    public String getAccountsConsentId(final String bankId) {
        final int tryCount = 3; boolean force = false;

        try {
            for (int ii = tryCount; 0 < ii--; ) {
                final String   accessToken = getClientAccessToken(bankId, force);
                final BankInfo bankInfo    = getTokenManager(bankId).getOauthconfig().getBankInfo();
                // Setup HTTP headers
                final HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                headers.add(X_FAPI_INTERACTION_ID, UUID.randomUUID().toString());
                final Consents consents = new Consents();
                consents.setPermissions(new ArrayList<>(AccountConsentPermissions.PERMISSIONS));
                LocalDateTime exp = LocalDateTime.now();
                exp = exp.plusYears(10);
                consents.setExpirationDateTime(exp);
                consents.setTransactionFromDateTime(LocalDateTime.now());
                consents.setTransactionToDateTime(exp);

                final ConsentsRequest consentsRequest = new ConsentsRequest(consents);

                final ObjectMapper mapper = new ObjectMapper(); final String json;
                try {
                    json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(consentsRequest);
                    LOG.info("Consent request: {}", json);
                } catch (final JsonProcessingException e) {
                    throw new APICallException("Error creating JSON: " + e.getLocalizedMessage());
                }
                // get ConsentID
                final HttpResponse httpResponse = HttpHelper
                        .doAPICall(HttpMethod.POST, new URL(bankInfo.getAccountsUrl() + "/account-access-consents"), headers
                        .toSingleValueMap(), json);

                // Sometimes WSO2 respond errors in xml
                final String content = httpResponse.getHttpRawContent(); HttpHelper.checkWSO2Errors(content);
                final int respondCode = httpResponse.getHttpResponseCode();
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
    public @NotNull AccessToken createAndSaveUserAccessToken(final TokenResponse refreshToken, final String bankId,
                                                             final String userName) {
        final AccessToken userAccessToken = new AccessToken();
        userAccessToken.setAccessToken(refreshToken.getAccessToken());
        userAccessToken.setAccessTokenType("user");
        userAccessToken.setExpires(refreshToken.getJwtExpires());
        userAccessToken.setRefreshToken(refreshToken.getRefreshToken());
        userAccessToken.setBankId(bankId);
        userAccessToken.setUserName(userName);

        // Remove previous
        accessTokenRepository.remove(bankId, userName);
        accessTokenRepository.save(userAccessToken);

        return userAccessToken;
    }


    /**
     * Handle accounts API request
     *
     * @param httpMethod
     * @param bankId
     * @param user
     * @param url
     * @param jsonContent
     * @return
     */
    protected @NotNull ResponseEntity<String> handleAccounts(final HttpMethod httpMethod, final String bankId,
                                                             final User user, final String url,
                                                             @Nullable final String jsonContent) {
        LOG.info("BankID: {} User {}", bankId, user);
        try {
            final String   userAccessToken = userAccessTokenIsValid(bankId, user.getUsername());
            final BankInfo bankInfo        = getTokenManager(bankId).getOauthconfig().getBankInfo();

            // Setup HTTP headers
            final HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(userAccessToken);
            headers.add(X_FAPI_INTERACTION_ID, UUID.randomUUID().toString());
            final URL apiURL = new URL(bankInfo.getAccountsUrl() + url);
            LOG.info("Call API: {}", apiURL);
            final HttpResponse httpResponse = HttpHelper.doAPICall(httpMethod, apiURL, headers
                    .toSingleValueMap(), jsonContent);

            // Sometimes WSO2 respond errors in xml
            final String content = httpResponse.getHttpRawContent(); HttpHelper.checkWSO2Errors(content);
            final int responseCode = httpResponse.getHttpResponseCode();
            if (responseCode < 200 || responseCode > 300) {
                LOG.error("Respond code {}; respond: [{}]", responseCode, content);
                // TODO Handle correctly if error will come processable
                // 500:java.lang.UnsupportedOperationException: User.....
                // 401:[{"fault":{"code":900901,"message":"Invalid Credentials","description":"Access failure for API: /open-banking/v3.1/aisp/v3.1.2, version: v3.1.2 status: (900901) - Invalid Credentials. Make sure you have given the correct access token"}}]
                // 403:[{"fault":{"code":900910,"message":"The access token does not allow you to access the requested resource","description":"Access failure for API: /open-banking/v3.1/aisp/v3.1.2, version: v3.1.2 status: (900910) - The access token does not allow you to access the requested resource"}}]
//                if ((HttpStatus.UNAUTHORIZED.value() == responseCode) || (HttpStatus.INTERNAL_SERVER_ERROR.value() == responseCode && content.startsWith("java.lang.UnsupportedOperationException: User"))) {
                final String consentId = getAccountsConsentId(bankId);
                throw new OAuthAuthorizationRequiredException(consentId);
//                }
            } final HttpStatus httpStatus = HttpStatus.resolve(responseCode);
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

    /**
     * Handle payments API request
     *
     * @param httpMethod
     * @param bankId
     * @param user
     * @param url
     * @param jsonContent
     * @param accessTokenType
     * @return
     */
    protected @NotNull ResponseEntity<String> handlePayments(final HttpMethod httpMethod, final String bankId,
                                                             final User user, final String url,
                                                             @Nullable final String jsonContent,
                                                             final WSO2Controller.ACCESS_TOKEN_TYPE accessTokenType) {
        LOG.info("BankID: {} User {}", bankId, user);
        try {
            final String accessToken;
            switch (accessTokenType) {
                case CLIENT:
                    accessToken = getClientAccessToken(bankId, true);
                    break;
                case USER:
                    accessToken = userAccessTokenIsValid(bankId, user.getUsername());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + accessTokenType);
            }
            final BankInfo bankInfo = getTokenManager(bankId).getOauthconfig().getBankInfo();

            // Setup HTTP headers
            final HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.add(X_FAPI_INTERACTION_ID, UUID.randomUUID().toString());
            final URL apiURL = new URL(bankInfo.getPaymentsUrl() + url);
            LOG.info("Call API with {} accessToken: {}", accessTokenType.name(), apiURL);
            final HttpResponse httpResponse = HttpHelper.doAPICall(httpMethod, apiURL, headers
                    .toSingleValueMap(), jsonContent);

            // Sometimes WSO2 respond errors in xml
            final String content = httpResponse.getHttpRawContent(); HttpHelper.checkWSO2Errors(content);
            final int respondCode = httpResponse.getHttpResponseCode();
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
            LOG.info("Cached Access ({}) token {} is expired {} expires {} current {}", bankId, accessToken
                    .getAccessToken(), accessToken.isExpired(), accessToken.getExpires(), System.currentTimeMillis());
        }

        if (force || null == accessToken || accessToken.isExpired()) {
            final TokenManager tokenManager = getTokenManager(bankId); final TokenResponse tokenResponse = tokenManager
                    .getAccessTokenWithClientCredential(); // new String[]{scope});
            final int respondeCode = tokenResponse.getHttpResponseCode();
            if (200 <= respondeCode && 300 > respondeCode) {

                final String accessTokenStr = tokenResponse.getAccessToken();
                LOG.debug("Client AccessToken: {}", accessTokenStr);

                // Save accessToken for later usage
                accessToken = new AccessToken();
                accessToken.setAccessToken(accessTokenStr);
                accessToken.setAccessTokenType("client");
                accessToken.setExpires(tokenResponse.getExpiresIn());
                accessToken.setBankId(bankId);

                LOG.info("New Access ({}) token {} is expired {} expires {} current {}", bankId, accessToken
                        .getAccessToken(), accessToken.isExpired(), accessToken.getExpires(), System
                                 .currentTimeMillis());

                clientAccessTokenCache.put(bankId, accessToken);
            } else {
                throw new APICallException(tokenResponse.getHttpRawContent());
            }
        }

        return accessToken.getAccessToken();
    }


    public enum ACCESS_TOKEN_TYPE {
        /**
         * Client aka TPP.
         */
        CLIENT,
        /**
         * End user, logged in bank user who accepted consent.
         */
        USER
    }
}
