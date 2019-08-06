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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/aisp/v1/")
public class PartiesController extends WSO2Controller {
    /**
     * Authorised User
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "party", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAuthorisedUser(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user) {
        return handleAccounts(HttpMethod.GET, bankId, user, "/party", null);
    }

    /**
     * Get Account Owner
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "accounts/{AccountId}/party", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAccountOwner(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(ACCOUNT_ID) final String accountId) {
        return handleAccounts(HttpMethod.GET, bankId, user, "/accounts/" + accountId + "/party", null);
    }

    /**
     * Get All Parties
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "accounts/{AccountId}/parties", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getParties(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(ACCOUNT_ID) final String accountId) {
        return handleAccounts(HttpMethod.GET, bankId, user, "/accounts/" + accountId + "/parties", null);
    }


}
