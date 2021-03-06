/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.common.http.oauth2.OAuthAuthorizationRequiredException;
import hu.dpc.common.http.oauth2.TokenResponse;
import hu.dpc.openbank.oauth2.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/token/v1/")
public class TokenController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @GetMapping(path = "/code/{Code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTokenCodeForAccounts(
            @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user,
            @PathVariable("Code") final String code) {
        LOG.info("GET /token/v1/code/{}    bankId={}", code, bankId); return getToken(bankId, user, code);
    }

    @GetMapping(path = "/code/{Code}/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTokenCodeForAccounts2(
            @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user,
            @PathVariable("Code") final String code) {
        LOG.info("GET /accounts/token/v1/code/{}    bankId={}", code, bankId); return getToken(bankId, user, code);
    }

    @GetMapping(path = "/code/{Code}/payments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTokenCodeForPayments(
            @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user,
            @PathVariable("Code") final String code) {
        LOG.info("GET /payments/token/v1/code/{}    bankId={}", code, bankId); return getToken(bankId, user, code);
    }

    public ResponseEntity<String> getToken(final String bankId, final User user, final String code) {
        final TokenManager  tokenManager        = getTokenManager(bankId);
        final TokenResponse accessTokenResponse = tokenManager.getAccessTokenFromCode(code);
        final int           responseCode        = accessTokenResponse.getHttpResponseCode();
        if (200 <= responseCode && 300 > responseCode) {
            createAndSaveUserAccessToken(accessTokenResponse, bankId, user.getUsername());
            return new ResponseEntity<>("", HttpStatus.OK);
        } LOG.warn("Code exchange not succeeded. HTTP[{}] RAWResponse [{}]", responseCode, accessTokenResponse
                .getHttpRawContent());
        final String consentId = getAccountsConsentId(bankId);
        LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
        throw new OAuthAuthorizationRequiredException(consentId);
    }

}
