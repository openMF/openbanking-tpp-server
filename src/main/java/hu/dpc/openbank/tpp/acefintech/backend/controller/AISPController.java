package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.openbank.tpp.acefintech.backend.enity.HttpResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.repository.OAuthAuthorizationRequiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping(path = "/aisp/v1/")
public class AISPController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(AISPController.class);

    private ResponseEntity handle(@NonNull String bankId, @NonNull User user, @NonNull String url) {
        LOG.info("BankID: {} User {}", bankId, user);
        try {
            String userAccessToken = userAccessTokenIsValid(bankId, user.getUsername());
            BankInfo bankInfo = getTokenManager(bankId).getOauthconfig().getBankInfo();

            // Setup HTTP headers
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + userAccessToken);
            headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());
            URL apiURL = new URL(bankInfo.getAccountsUrl() + url);
            LOG.info("Call API: {}", apiURL);
            HttpResponse httpResponse = doAPICall(false, apiURL, headers, null);

            // Sometimes WSO2 respond errors in xml
            String content = httpResponse.getContent().trim();
            checkWSO2Errors(content);
            int respondCode = httpResponse.getResponseCode();
            if (!(respondCode >= 200 && respondCode < 300)) {
                LOG.error("Respond code {}; respond: [{}]", respondCode, content);
            }
            HttpStatus httpStatus = HttpStatus.resolve(respondCode);
            return new ResponseEntity<>(content, null == httpStatus ? HttpStatus.BAD_REQUEST : httpStatus);
        } catch (OAuthAuthorizationRequiredException oare) {
            LOG.warn("Something went wrong!", oare);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("x-tpp-consentid", oare.getConsentId());
            return new ResponseEntity<>("Require authorize", responseHeaders, HttpStatus.PRECONDITION_REQUIRED);
        } catch (Throwable e) {
            // Intended to catch Throwable
            LOG.error("Something went wrong!", e);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * GetAccounts
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "/accounts", produces = "application/json")
    public ResponseEntity getAccounts(@RequestHeader(value = "x-tpp-bankid") String bankId, @AuthenticationPrincipal User user) {
        return handle(bankId, user, "/accounts");
    }

    /**
     * Get one account
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "/accounts/{AccountId}", produces = "application/json")
    public ResponseEntity getAccount(@RequestHeader(value = "x-tpp-bankid") String bankId, @AuthenticationPrincipal User user, @PathVariable("AccountId") String accountId) {
        return handle(bankId, user, "/accounts/" + accountId);
    }

    /**
     * Get balances
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "/balances", produces = "application/json")
    public ResponseEntity getBalances(@RequestHeader(value = "x-tpp-bankid") String bankId, @AuthenticationPrincipal User user) {
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
    @GetMapping(path = "/accounts/{AccountId}/balances", produces = "application/json")
    public ResponseEntity getBalance(@RequestHeader(value = "x-tpp-bankid") String bankId, @AuthenticationPrincipal User user, @PathVariable("AccountId") String accountId) {
        return handle(bankId, user, "/accounts/" + accountId + "/balances");
    }


}
