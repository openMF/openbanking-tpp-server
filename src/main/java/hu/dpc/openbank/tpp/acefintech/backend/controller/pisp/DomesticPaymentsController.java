/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller.pisp;


import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/pisp/v1/")
public class DomesticPaymentsController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(DomesticPaymentsController.class);


    /**
     * @param bankId
     * @param user
     * @return
     */
    @PostMapping(path = "preparePayment", produces = APPLICATION_JSON)
    public ResponseEntity<String> preparePayment(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @RequestBody final String body) {
// TODO Create Consent
// TODO Return consentId and other variables to GUI initiate /authorize request
        return new ResponseEntity<String>("", HttpStatus.OK);
    }


    /**
     * @param bankId
     * @param user
     * @return
     */
    @PostMapping(path = "executePayment/{ConsentId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> executePayment(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(CONSENT_ID) final String consentId) {
// TODO Find payment based on consentId
// TODO Execute payment
// TODO status
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    /**
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "domestic-payment-consents", produces = APPLICATION_JSON)
    public ResponseEntity<String> getDomesticPaymentsConsents(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user) {
// TODO
        return handle(WSO2Controller.HTTP_METHOD.POST, bankId, user, "/domestic-payment-consents", null);
    }

    /**
     * @param bankId
     * @param user
     * @param consentId
     * @return
     */
    @GetMapping(path = "domestic-payment-consents/{ConsentId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> getDomesticPaymentsConsent(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(CONSENT_ID) final String consentId) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/domestic-payment-consents/" + consentId, null);
    }

    /**
     * @param bankId
     * @param user
     * @param consentId
     * @return
     */
    @GetMapping(path = "domestic-payment-consents/{ConsentId}/funds-confirmation", produces = APPLICATION_JSON)
    public ResponseEntity<String> getDomesticPaymentConsentFundConfirmation(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(CONSENT_ID) final String consentId) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/domestic-payment-consents/" + consentId + "/funds-confirmation", null);
    }


    /**
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "domestic-payments", produces = APPLICATION_JSON)
    public ResponseEntity<String> getDomesticPayments(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user) {
// TODO
        return handle(WSO2Controller.HTTP_METHOD.POST, bankId, user, "/domestic-payments", null);
    }

    /**
     * @param bankId
     * @param user
     * @param domesticPaymentId
     * @return
     */
    @GetMapping(path = "domestic-payments/{DomesticPaymentId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> getDomesticPayment(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable("DomesticPaymentId") final String domesticPaymentId) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/domestic-payments/" + domesticPaymentId, null);
    }


    /**
     * @param bankId
     * @param user
     * @param domesticPaymentId
     * @return
     */
    @GetMapping(path = "domestic-payments/{DomesticPaymentId}/payment-details", produces = APPLICATION_JSON)
    public ResponseEntity<String> getDomesticPaymentDetails(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable("DomesticPaymentId") final String domesticPaymentId) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/domestic-payments/" + domesticPaymentId + "/payment-details", null);
    }
}
