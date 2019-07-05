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
public class AccountsController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsController.class);

    /**
     * GetAccounts
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "accounts", produces = APPLICATION_JSON)
    public ResponseEntity<String> getAccounts(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/accounts", null);
    }


    /**
     * Get one account
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "accounts/{AccountId}", produces = APPLICATION_JSON)
    public ResponseEntity<String> getAccount(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(ACCOUNT_ID) final String accountId) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/accounts/" + accountId, null);
    }

}
