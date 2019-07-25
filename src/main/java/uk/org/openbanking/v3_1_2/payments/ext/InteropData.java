/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package uk.org.openbanking.v3_1_2.payments.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InteropData {

    @JsonProperty("amountType")
    private String amountType;
    @JsonProperty("note")
    private String note;
    @JsonProperty("transactionType")
    private InteropTransactionType transactionType;

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(final String amountType) {
        this.amountType = amountType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    public InteropTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(final InteropTransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
