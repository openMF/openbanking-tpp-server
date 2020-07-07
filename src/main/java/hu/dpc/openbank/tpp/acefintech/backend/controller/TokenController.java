/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.common.http.oauth2.TokenResponse;
import hu.dpc.openbank.exceptions.OAuthAuthorizationRequiredException;
import hu.dpc.openbank.oauth2.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/token/v1/")
public class TokenController extends WSO2Controller {

  @GetMapping(path = "/code/{Code}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getTokenCodeForAccounts(
      @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user,
      @PathVariable("Code") final String code) {
    log.info("GET /token/v1/code/{}    bankId={}", code, bankId);
    return getToken(bankId, user, code);
  }


  @GetMapping(path = "/code/{Code}/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getTokenCodeForAccounts2(
      @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user,
      @PathVariable("Code") final String code) {
    log.info("GET /accounts/token/v1/code/{}    bankId={}", code, bankId);
    return getToken(bankId, user, code);
  }


  @GetMapping(path = "/code/{Code}/payments", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getTokenCodeForPayments(
      @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user,
      @PathVariable("Code") final String code) {
    log.info("GET /payments/token/v1/code/{}    bankId={}", code, bankId);
    return getToken(bankId, user, code);
  }


  private ResponseEntity<String> getToken(final String bankId, final User user, final String code) {
    final TokenManager tokenManager = getTokenManager(bankId);
    final TokenResponse accessTokenResponse = tokenManager.getAccessTokenFromCode(code);
    final int responseCode = accessTokenResponse.getHttpResponseCode();
    if (200 <= responseCode && 300 > responseCode) {
      createAndSaveUserAccessToken(accessTokenResponse, bankId, user.getUsername());
      return new ResponseEntity<>("", HttpStatus.OK);
    }
    log.warn("Code exchange not succeeded. HTTP[{}] RAWResponse [{}]", responseCode, accessTokenResponse.getHttpRawContent());
    final String consentId = getAccountsConsentId(bankId);
    log.info("No user AccessToken exists. OAuth authorization required! ConsentID: [{}]", consentId);
    throw new OAuthAuthorizationRequiredException(consentId);
  }

}
