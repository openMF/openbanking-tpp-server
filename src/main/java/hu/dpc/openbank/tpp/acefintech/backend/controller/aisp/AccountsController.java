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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/aisp/v1/")
public class AccountsController extends WSO2Controller {

  /**
   * GetAccounts.
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805362/Accounts+v3.1.2#Accountsv3.1.2-GET/accounts
   */
  @GetMapping(path = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getAccounts(@RequestHeader(WSO2Controller.X_TPP_BANKID) final @Nonnull String bankId,
      @AuthenticationPrincipal final @Nonnull User user) {
    return handleAccounts(HttpMethod.GET, bankId, user, "/accounts");
  }


  /**
   * Get one account
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805362/Accounts+v3.1.2#Accountsv3.1.2-GET/accounts/{AccountId}
   */
  @GetMapping(path = "accounts/{AccountId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getAccount(@RequestHeader(WSO2Controller.X_TPP_BANKID) final @Nonnull String bankId,
      @AuthenticationPrincipal final @Nonnull User user, @PathVariable(ACCOUNT_ID) final String accountId) {
    return handleAccounts(HttpMethod.GET, bankId, user, "/accounts/" + accountId);
  }

}
