/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller.pisp;


import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.common.http.HttpHelper;
import hu.dpc.common.http.HttpResponse;
import hu.dpc.openbank.exceptions.APICallException;
import hu.dpc.openbank.exceptions.BankConfigException;
import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
import hu.dpc.openbank.tpp.acefintech.backend.entity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.entity.bank.PaymentConsent;
import hu.dpc.openbank.tpp.acefintech.backend.repository.PaymentConsentRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.org.openbanking.v3_1_2.payments.OBWriteDomesticConsentResponse3;
import uk.org.openbanking.v3_1_2.payments.OBWriteDomesticResponse3;

@Slf4j
@RestController
@RequestMapping(path = "/pisp/v1/")
public class DomesticPaymentsController extends WSO2Controller {

  private final PaymentConsentRepository paymentConsentRepository;
  private final ObjectMapper objectMapper;


  public DomesticPaymentsController(final PaymentConsentRepository paymentConsentRepository,
      final ObjectMapper objectMapper) {
    this.paymentConsentRepository = paymentConsentRepository;
    this.objectMapper = objectMapper;
  }


  /**
   * Get Payments ConsentId
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805881/Domestic+Payments+v3.1.2#DomesticPaymentsv3.1.2-POST/domestic-payment-consents
   */
  @PostMapping(path = "preparePayment")
  public ResponseEntity<String> preparePayment(
      @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user,
      @RequestBody final String body) {

    log.info("preparePayment called bankid={} userName={}", bankId, user.getUsername());
    final OBWriteDomesticConsentResponse3 response = getConsentId(bankId, body);
    final String consentId = response.getData().getConsentId();

    final PaymentConsent paymentConsent = new PaymentConsent();
    paymentConsent.setBankId(bankId);
    paymentConsent.setConsentId(consentId);
    paymentConsent.setConsentResponse(response.getRawContent());
    paymentConsentRepository.save(paymentConsent);

    final MultiValueMap<String, String> headers = new HttpHeaders();
    headers.add("x-tpp-consentid", consentId);
    return new ResponseEntity<>("", headers, HttpStatus.OK);
  }


  /**
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805881/Domestic+Payments+v3.1.2#DomesticPaymentsv3.1.2-POST/domestic-payments
   */
  @PostMapping(path = "executePayment/{ConsentId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> executePayment(
      @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user,
      @PathVariable(CONSENT_ID) final String consentId) {

    log.info("executePayment called bankid={} consentId={} userName={}", bankId, consentId, user.getUsername());
    final PaymentConsent paymentConsent;
    try {
      paymentConsent = paymentConsentRepository.getConsent(bankId, consentId);
      if (null == paymentConsent) {
        throw new EntityNotFoundException();
      }
    } catch (final EntityNotFoundException nfe) {
      return new ResponseEntity<>("{\"error\":\"consentId not found\"}", HttpStatus.PRECONDITION_FAILED);
    }

    String modifiedResponse = null;
    try {
      final OBWriteDomesticConsentResponse3 prevResponse = objectMapper.readValue(paymentConsent
          .getConsentResponse(), OBWriteDomesticConsentResponse3.class);
      prevResponse.getData().setStatus(null);
      prevResponse.getData().setCreationDateTime(null);
      prevResponse.getData().setStatusUpdateDateTime(null);
      modifiedResponse = objectMapper.writeValueAsString(prevResponse);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    final ResponseEntity<String> result = handlePayments(HttpMethod.POST, bankId, user, "/domestic-payments", modifiedResponse,
        WSO2Controller.ACCESS_TOKEN_TYPE.USER);

    if (result.getStatusCode() == HttpStatus.CREATED) {
      final OBWriteDomesticResponse3 domesticResult;
      try {
        domesticResult = objectMapper.readValue(result.getBody(), OBWriteDomesticResponse3.class);
        paymentConsent.setPaymentId(domesticResult.getData().getDomesticPaymentId());
        paymentConsentRepository.save(paymentConsent);
      } catch (final IOException e) {
        return new ResponseEntity<>("{\"error\":\"domestic-payments execution problem\"}", HttpStatus.PRECONDITION_FAILED);
      }
    }

    return result;
  }


  /**
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805881/Domestic+Payments+v3.1.2#DomesticPaymentsv3.1.2-GET/domestic-payments/{DomesticPaymentId}
   */
  @GetMapping(path = "payment/{DomesticPaymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getPaymentDetails(
      @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user,
      @PathVariable("DomesticPaymentId") final String domesticPaymentId) {

    return handlePayments(HttpMethod.GET, bankId, user, "/domestic-payments/" + domesticPaymentId, null,
        WSO2Controller.ACCESS_TOKEN_TYPE.CLIENT);
  }


  /**
   * Get Payments ConsentId
   *
   * @return consentId if request it was not success return empty.
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805881/Domestic+Payments+v3.1.2#DomesticPaymentsv3.1.2-POST/domestic-payment-consents
   */
  private OBWriteDomesticConsentResponse3 getConsentId(final @Nonnull String bankId, final String body) {
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

        // get ConsentID
        final HttpResponse httpResponse = HttpHelper
            .doAPICall(HttpMethod.POST, new URL(bankInfo.getPaymentsUrl() + "/domestic-payment-consents"), headers, body);

        // Sometimes WSO2 respond errors in xml
        final String content = httpResponse.getHttpRawContent();
        HttpHelper.checkWSO2Errors(content);
        final int respondCode = httpResponse.getHttpResponseCode();
        if (200 <= respondCode && 300 > respondCode) {
          log.info("Respond code {}; respond: [{}]", respondCode, content);
          final OBWriteDomesticConsentResponse3 result = objectMapper.readValue(content, OBWriteDomesticConsentResponse3.class);
          result.setRawContent(content);
          return result;
        }
        log.error("Respond code {}; respond: [{}]", respondCode, content);
        force = true;
      }

      throw new APICallException("ConsentID request fails!");
    } catch (final MalformedURLException mue) {
      log.error("URL problems!", mue);
      throw new BankConfigException(mue.getLocalizedMessage());
    } catch (final Exception e) {
      log.error("Process error!", e);
      throw new BankConfigException(e.getLocalizedMessage());
    }
  }

}
