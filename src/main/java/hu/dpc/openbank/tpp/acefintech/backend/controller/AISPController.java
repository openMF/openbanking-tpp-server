/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.openbank.tpp.acefintech.backend.enity.HttpResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.repository.OAuthAuthorizationRequiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/aisp/v1/")
public class AISPController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(AISPController.class);

    /**
     * GetAccounts
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "accounts", produces = "application/json")
    public ResponseEntity<String> getAccounts(@RequestHeader("x-tpp-bankid") final String bankId, @AuthenticationPrincipal final User user) {
        return handle(bankId, user, "/accounts");
    }

    private ResponseEntity<String> handle(final String bankId, final User user, final String url) {
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
            final HttpResponse httpResponse = hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller.doAPICall(false, apiURL, headers, null);

            // Sometimes WSO2 respond errors in xml
            final String content = httpResponse.getContent();
            hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller.checkWSO2Errors(content);
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

    /**
     * Get one account
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "accounts/{AccountId}", produces = "application/json")
    public ResponseEntity<String> getAccount(@RequestHeader("x-tpp-bankid") final String bankId, @AuthenticationPrincipal final User user, @PathVariable("AccountId") final String accountId) {
        return handle(bankId, user, "/accounts/" + accountId);
    }

    /**
     * Get balances
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "balances", produces = "application/json")
    public ResponseEntity<String> getBalances(@RequestHeader("x-tpp-bankid") final String bankId, @AuthenticationPrincipal final User user) {
        return handle(bankId, user, "/balances");
    }

    /**
     * Get one account balance
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "accounts/{AccountId}/balances", produces = "application/json")
    public ResponseEntity<String> getBalance(@RequestHeader("x-tpp-bankid") final String bankId, @AuthenticationPrincipal final User user, @PathVariable("AccountId") final String accountId) {
        return handle(bankId, user, "/accounts/" + accountId + "/balances");
    }

}
