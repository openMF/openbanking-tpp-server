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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(final String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(final Amount amount) {
        this.amount = amount;
    }

    public String getStatementReference() {
        return statementReference;
    }

    public void setStatementReference(final String statementReference) {
        this.statementReference = statementReference;
    }

    public String getCreditDebitIndicator() {
        return creditDebitIndicator;
    }

    public void setCreditDebitIndicator(final String creditDebitIndicator) {
        this.creditDebitIndicator = creditDebitIndicator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(final String bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public String getValueDateTime() {
        return valueDateTime;
    }

    public void setValueDateTime(final String valueDateTime) {
        this.valueDateTime = valueDateTime;
    }

    public String getTransactionInformation() {
        return transactionInformation;
    }

    public void setTransactionInformation(final String transactionInformation) {
        this.transactionInformation = transactionInformation;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(final String addressLine) {
        this.addressLine = addressLine;
    }

    public BankTransactionCode getBankTransactionCode() {
        return bankTransactionCode;
    }

    public void setBankTransactionCode(final BankTransactionCode bankTransactionCode) {
        this.bankTransactionCode = bankTransactionCode;
    }

    public ProprietaryBankTransactionCode getProprietaryBankTransactionCode() {
        return proprietaryBankTransactionCode;
    }

    public void setProprietaryBankTransactionCode(final ProprietaryBankTransactionCode proprietaryBankTransactionCode) {
        this.proprietaryBankTransactionCode = proprietaryBankTransactionCode;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(final Balance balance) {
        this.balance = balance;
    }
}
