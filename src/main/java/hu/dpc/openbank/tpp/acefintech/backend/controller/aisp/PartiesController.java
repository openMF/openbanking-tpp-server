/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller.aisp;


import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
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
public class PartiesController extends WSO2Controller {

  /**
   * Authorised User
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805687/Parties+v3.1.2#Partiesv3.1.2-GET/party
   */
  @GetMapping(path = "party", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getAuthorisedUser(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user) {
    return handleAccounts(HttpMethod.GET, bankId, user, "/party");
  }


  /**
   * Get Account Owner
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805687/Parties+v3.1.2#Partiesv3.1.2-GET/accounts/{AccountId}/party
   */
  @GetMapping(path = "accounts/{AccountId}/party", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getAccountOwner(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user, @PathVariable(ACCOUNT_ID) final String accountId) {
    return handleAccounts(HttpMethod.GET, bankId, user, "/accounts/" + accountId + "/party");
  }


  /**
   * Get All Parties
   *
   * @link https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077805687/Parties+v3.1.2#Partiesv3.1.2-GET/accounts/{AccountId}/parties
   */
  @GetMapping(path = "accounts/{AccountId}/parties", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getParties(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId,
      @AuthenticationPrincipal final User user, @PathVariable(ACCOUNT_ID) final String accountId) {
    return handleAccounts(HttpMethod.GET, bankId, user, "/accounts/" + accountId + "/parties");
  }


}
