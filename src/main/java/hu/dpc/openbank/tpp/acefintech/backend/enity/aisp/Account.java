/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.enity.aisp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
    /**
     * <pre>
     * HTTP/1.1 200 OK
     * x-fapi-interaction-id: 93bac548-d2de-4546-b106-880a5018460d
     * Content-Type: application/json
     *
     * {
     *   "Data": {
     *     "Account": [
     *       {
     *         "AccountId": "22289",
     *         "Status": "Enabled",
     *         "StatusUpdateDateTime": "2019-01-01T06:06:06+00:00",
     *         "Currency": "GBP",
     *         "AccountType": "Personal",
     *         "AccountSubType": "CurrentAccount",
     *         "Nickname": "Bills",
     *         "Account": [
     *             {
     *                 "SchemeName": "UK.OBIE.SortCodeAccountNumber",
     *                 "Identification": "80200110203345",
     *                 "Name": "Mr Kevin",
     *                 "SecondaryIdentification": "00021"
     *             }
     *         ]
     *       },
     *       {
     *         "AccountId": "31820",
     *         "Status": "Enabled",
     *         "StatusUpdateDateTime": "2018-01-01T06:06:06+00:00",
     *         "Currency": "GBP",
     *         "AccountType": "Personal",
     *         "AccountSubType": "CurrentAccount",
     *         "Nickname": "Household",
     *         "Account": [
     *             {
     *                 "SchemeName": "UK.OBIE.SortCodeAccountNumber",
     *                 "Identification": "80200110203348",
     *                 "Name": "Mr Kevin"
     *             }
     *         ]
     *       }
     *     ]
     *   },
     *   "Links": {
     *     "Self": "https://api.alphabank.com/open-banking/v3.1/aisp/accounts/"
     *   },
     *   "Meta": {
     *     "TotalPages": 1
     *   }
     * }
     * </pre>
     */

    @JsonProperty("AccountId")
    private String accountId;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("StatusUpdateDateTime")
    private String statusUpdateDateTime;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("AccountSubType")
    private String accountSubType;
    @JsonProperty("Nickname")
    private String nickname;
    @JsonProperty("Account")
    private List<AccountAccount> account;

  
}
