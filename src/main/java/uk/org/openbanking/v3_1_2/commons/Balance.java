/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package uk.org.openbanking.v3_1_2.commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountAccount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Balance {
    @JsonProperty("AccountId")
    private String accountId;
    @JsonProperty("Amount")
    private Amount amount;
    @JsonProperty("CreditDebitIndicator")
    private String creditDebitIndicator;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("DateTime")
    private String dateTime;
    @JsonProperty("CreditLine")
    private List<CreditLine> creditLines;

}
