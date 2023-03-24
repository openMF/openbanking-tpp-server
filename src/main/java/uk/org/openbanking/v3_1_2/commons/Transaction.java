/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package uk.org.openbanking.v3_1_2.commons;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountAccount;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Transaction {
    @JsonProperty("AccountId")
    private String accountId;
    @JsonProperty("TransactionId")
    private String transactionId;
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("Amount")
    private Amount amount;
    @JsonProperty("StatementReference")
    private String statementReference;
    @JsonProperty("CreditDebitIndicator")
    private String creditDebitIndicator;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("BookingDateTime")
    private String bookingDateTime;
    @JsonProperty("ValueDateTime")
    private String valueDateTime;
    @JsonProperty("TransactionInformation")
    private String transactionInformation;
    @JsonProperty("AddressLine")
    private String addressLine;
    @JsonProperty("BankTransactionCode")
    private BankTransactionCode bankTransactionCode;
    @JsonProperty("ProprietaryBankTransactionCode")
    private ProprietaryBankTransactionCode proprietaryBankTransactionCode;
    @JsonProperty("Balance")
    private Balance balance;

  
}
