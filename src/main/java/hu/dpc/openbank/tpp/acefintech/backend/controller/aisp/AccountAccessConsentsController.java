/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller.aisp;


import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/aisp/v1/account-access-consents")
public class AccountAccessConsentsController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(AccountAccessConsentsController.class);

    /**
     * Get Account Access Consents
     *
     * @param bankId
     * @param user
     * @param consentId
     * @return
     */
    @GetMapping(path = "/{ConsentId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> getConsent(@RequestHeader(X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(CONSENT_ID) final String consentId) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/account-access-consents/" + consentId, null);
    }

    /**
     * Get Account Access Consents
     *
     * @param bankId
     * @param user
     * @param consentId
     * @return
     */
    @DeleteMapping(path = "/{ConsentId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> deleteConsent(@RequestHeader(X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(CONSENT_ID) final String consentId) {
        return handle(WSO2Controller.HTTP_METHOD.DELETE, bankId, user, "/account-access-consents/" + consentId, null);
    }

}
