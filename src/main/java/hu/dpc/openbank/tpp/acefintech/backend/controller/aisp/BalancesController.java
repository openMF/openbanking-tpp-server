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
@RequestMapping(path = "/aisp/v1/")
public class BalancesController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(BalancesController.class);

    /**
     * Get balances
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "balances", produces = APPLICATION_JSON)
    public ResponseEntity<String> getBalances(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user) {
        return handleAccounts(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/balances", null);
    }

    /**
     * Get one account balance
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "accounts/{AccountId}/balances", produces = APPLICATION_JSON)
    public ResponseEntity<String> getAccountBalance(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(ACCOUNT_ID) final String accountId) {
        return handleAccounts(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/accounts/" + accountId + "/balances", null);
    }

}
