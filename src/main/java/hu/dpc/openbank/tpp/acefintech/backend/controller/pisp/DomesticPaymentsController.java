/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller.pisp;


import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
import hu.dpc.openbank.tpp.acefintech.backend.enity.HttpResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.PaymentConsent;
import hu.dpc.openbank.tpp.acefintech.backend.repository.APICallException;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankConfigException;
import hu.dpc.openbank.tpp.acefintech.backend.repository.PaymentConsentRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import uk.org.openbanking.v3_1_2.payments.OBWriteDomesticConsentResponse3;
import uk.org.openbanking.v3_1_2.payments.OBWriteDomesticResponse3;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/pisp/v1/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class DomesticPaymentsController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(DomesticPaymentsController.class);

    @Autowired
    private PaymentConsentRepository paymentConsentRepository;

    /**
     * @param bankId
     * @param user
     * @return
     */
    @PostMapping(path = "preparePayment")
    public ResponseEntity<String> preparePayment(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @RequestBody final String body) {
        LOG.info("preparePayment called bankid={} userName={}", bankId, user.getUsername());
        final OBWriteDomesticConsentResponse3 response = getConsentId(bankId, body);
        final String consentId = response.getData().getConsentId();

        final PaymentConsent paymentConsent = new PaymentConsent();
        paymentConsent.setBankId(bankId);
        paymentConsent.setConsentId(consentId);
        paymentConsent.setConsentResponse(body);
        paymentConsentRepository.save(paymentConsent);

        final MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("x-tpp-consentid", consentId);
        return new ResponseEntity<String>("", headers, HttpStatus.OK);
    }


    /**
     * @param bankId
     * @param user
     * @return
     */
    @PostMapping(path = "executePayment/{ConsentId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> executePayment(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(CONSENT_ID) final String consentId) {
        LOG.info("executePayment called bankid={} consentId={} userName={}", bankId, consentId, user.getUsername());
        final PaymentConsent paymentConsent;
        try {
            paymentConsent = paymentConsentRepository.getConsent(bankId, consentId);
            if (null == paymentConsent) {
                throw new EntityNotFoundException();
            }
        } catch (final EntityNotFoundException nfe) {
            return new ResponseEntity<String>("{\"error\":\"consentId not found\"}", HttpStatus.PRECONDITION_FAILED);
        }

        final ResponseEntity<String> result = handlePayments(WSO2Controller.HTTP_METHOD.POST, bankId, user, "/domestic-payments", paymentConsent.getConsentResponse());

        if (result.getStatusCode() == HttpStatus.CREATED) {
            final ObjectMapper mapper = new ObjectMapper();
            OBWriteDomesticResponse3 domesticResult = null;
            try {
                domesticResult = mapper.readValue(result.getBody(), OBWriteDomesticResponse3.class);
                paymentConsent.setPaymentId(domesticResult.getData().getDomesticPaymentId());
                paymentConsentRepository.save(paymentConsent);
            } catch (final IOException e) {
                return new ResponseEntity<String>("{\"error\":\"domestic-payments execution problem\"}", HttpStatus.PRECONDITION_FAILED);
            }
        }

        return result;
    }

    /**
     * @param bankId
     * @param user
     * @param domesticPaymentId
     * @return
     */
    @GetMapping(path = "payment/{DomesticPaymentId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> getPaymentDetails(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable("DomesticPaymentId") final String domesticPaymentId) {
        return handlePayments(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/domestic-payments/" + domesticPaymentId, null);
    }

    /**
     * Get Payments ConsentId
     *
     * @param bankId
     * @return consentId if request it was not success return empty.
     */
    public OBWriteDomesticConsentResponse3 getConsentId(final @NotNull String bankId, final String body) {
        final int tryCount = 3;
        boolean force = false;

        try {
            for (int ii = tryCount; 0 < ii--; ) {
                final String accessToken = getClientAccessTokenForPayments(bankId, force);
                final BankInfo bankInfo = getTokenManager(bankId).getOauthconfig().getBankInfo();
                // Setup HTTP headers
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());

                // get ConsentID
                final HttpResponse httpResponse = doAPICall(WSO2Controller.HTTP_METHOD.POST, new URL(bankInfo.getPaymentsUrl() + "/domestic-payment-consents"), headers, body);

                // Sometimes WSO2 respond errors in xml
                final String content = httpResponse.getContent();
                checkWSO2Errors(content);
                final int respondCode = httpResponse.getResponseCode();
                if (200 <= respondCode && 300 > respondCode) {
                    LOG.info("Respond code {}; respond: [{}]", respondCode, content);
                    final ObjectMapper mapper = new ObjectMapper();
                    final OBWriteDomesticConsentResponse3 response = mapper.readValue(content, OBWriteDomesticConsentResponse3.class);
                    return response;
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

}
