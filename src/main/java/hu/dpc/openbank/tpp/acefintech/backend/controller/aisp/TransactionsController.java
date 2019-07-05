/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller.aisp;


import hu.dpc.openbank.tpp.acefintech.backend.controller.WSO2Controller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/aisp/v1/")
public class TransactionsController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionsController.class);

    /**
     * Get transactions
     *
     * @param bankId
     * @param user
     * @return
     */
    @GetMapping(path = "transactions", produces = APPLICATION_JSON)
    public ResponseEntity<String> getTransactions(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @RequestParam(name = "fromBookingDateTime", required = false) final String fromBookingDateTime, @RequestParam(name = "fromBookingDateTime", required = false) final String toBookingDateTime) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/transactions" + createParams(fromBookingDateTime, toBookingDateTime), null);
    }


    /**
     * Get Account Transactions
     *
     * @param bankId
     * @param user
     * @param accountId
     * @return
     */
    @GetMapping(path = "accounts/{AccountId}/transactions", produces = APPLICATION_JSON)
    public ResponseEntity<String> getAccountTransactions(@RequestHeader(WSO2Controller.X_TPP_BANKID) final String bankId, @AuthenticationPrincipal final User user, @PathVariable(ACCOUNT_ID) final String accountId, @RequestParam(name = "fromBookingDateTime", required = false) final String fromBookingDateTime, @RequestParam(name = "fromBookingDateTime", required = false) final String toBookingDateTime) {
        return handle(WSO2Controller.HTTP_METHOD.GET, bankId, user, "/accounts/" + accountId + "/transactions" + createParams(fromBookingDateTime, toBookingDateTime), null);
    }

    private @NotNull String createParams(final String fromBookingDateTime, final String toBookingDateTime) {
        String queryParams = "";
        if (null != fromBookingDateTime && !fromBookingDateTime.isEmpty()) {
            queryParams = "fromBookingDateTime=" + fromBookingDateTime;
        }
        if (null != toBookingDateTime && !toBookingDateTime.isEmpty()) {
            if (!queryParams.isEmpty()) {
                queryParams += '&';
            }
            queryParams += "toBookingDateTime=" + toBookingDateTime;
        }

        if (!queryParams.isEmpty()) {
            queryParams = '?' + queryParams;
        }
        return queryParams;
    }

}
