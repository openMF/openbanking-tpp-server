/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller.aisp;


import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TransactionsController extends WSO2Controller {

  /**
   * Get transactions
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805388/Transactions+v3.1.2#Transactionsv3.1.2-GET/transactions
   */
  @GetMapping(path = "/aisp/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getTransactions(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user,
      @RequestParam(value = "fromBookingDateTime", required = false) final String fromBookingDateTime,
      @RequestParam(value = "toBookingDateTime", required = false) final String toBookingDateTime) {
    log.info("Called via Controller/RequestMapping /aisp/v1/transactions?fromBookingDateTime={}&toBookingDateTime={}", fromBookingDateTime,
        toBookingDateTime);
    return handleAccounts(HttpMethod.GET, bankId, user, "/transactions" + createParams(fromBookingDateTime, toBookingDateTime));
  }


  /**
   * Get Account Transactions
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805388/Transactions+v3.1.2#Transactionsv3.1.2-GET/accounts/{AccountId}/transactions
   */
  @GetMapping(path = "/aisp/v1/accounts/{AccountId}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getAccountTransactions(
      @RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user,
      @PathVariable(ACCOUNT_ID) final String accountId,
      @RequestParam(name = "fromBookingDateTime", required = false) final String fromBookingDateTime,
      @RequestParam(name = "toBookingDateTime", required = false) final String toBookingDateTime) {
    log.info("Called /aisp/v1/accounts/{AccountId}/transactions?fromBookingDateTime={}&fromBookingDateTime={}", fromBookingDateTime,
        fromBookingDateTime);
    return handleAccounts(HttpMethod.GET, bankId, user,
        "/accounts/" + accountId + "/transactions" + createParams(fromBookingDateTime, toBookingDateTime));
  }


  @Nonnull
  private String createParams(final String fromBookingDateTime, final String toBookingDateTime) {
    final StringBuilder queryParams = new StringBuilder();
    if (null != fromBookingDateTime && !fromBookingDateTime.isEmpty()) {
      queryParams.append("fromBookingDateTime=").append(fromBookingDateTime);
    }
    if (null != toBookingDateTime && !toBookingDateTime.isEmpty()) {
      if (0 == queryParams.length()) {
        queryParams.append('&');
      }
      queryParams.append("toBookingDateTime=").append(toBookingDateTime);
    }

    if (0 == queryParams.length()) {
      queryParams.append('?').append(queryParams);
    }
    return queryParams.toString();
  }

}
