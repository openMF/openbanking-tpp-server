/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package uk.org.openbanking.v3_1_2.payments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.org.openbanking.v3_1_2.commons.Amount;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Initation {
    @JsonProperty("InstructionIdentification")
    private String instructionIdentification;
    @JsonProperty("EndToEndIdentification")
    private String endToEndIdentification;
    @JsonProperty("InstructedAmount")
    private Amount instructedAmount;
    @JsonProperty("DebtorAccount")
    private DebtorAccount debtorAccount;
    @JsonProperty("CreditorAccount")
    private CreditAccount creditAccount;
    @JsonProperty("RemittanceInformation")
    private RemittanceInformation remittanceInformation;

    public String getInstructionIdentification() {
        return instructionIdentification;
    }

    public void setInstructionIdentification(final String instructionIdentification) {
        this.instructionIdentification = instructionIdentification;
    }

    public String getEndToEndIdentification() {
        return endToEndIdentification;
    }

    public void setEndToEndIdentification(final String endToEndIdentification) {
        this.endToEndIdentification = endToEndIdentification;
    }

    public Amount getInstructedAmount() {
        return instructedAmount;
    }

    public void setInstructedAmount(final Amount instructedAmount) {
        this.instructedAmount = instructedAmount;
    }

    public DebtorAccount getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(final DebtorAccount debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public CreditAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(final CreditAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    public RemittanceInformation getRemittanceInformation() {
        return remittanceInformation;
    }

    public void setRemittanceInformation(final RemittanceInformation remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }
}
