/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.enity.aisp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.dpc.openbank.tpp.acefintech.backend.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.openbank.tpp.acefintech.backend.rest.parser.LocalFormatDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consents {
    @JsonProperty("Permissions")
    public List<String> permissions;
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    @JsonProperty("ExpirationDateTime")
    public LocalDateTime expirationDateTime;
    @JsonProperty("TransactionFromDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    public LocalDateTime transactionFromDateTime;
    @JsonProperty("TransactionToDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    public LocalDateTime transactionToDateTime;
    @JsonProperty("ConsentId")
    private String consentId;

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(final String consentId) {
        this.consentId = consentId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<String> permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(final LocalDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public LocalDateTime getTransactionFromDateTime() {
        return transactionFromDateTime;
    }

    public void setTransactionFromDateTime(final LocalDateTime transactionFromDateTime) {
        this.transactionFromDateTime = transactionFromDateTime;
    }

    public LocalDateTime getTransactionToDateTime() {
        return transactionToDateTime;
    }

    public void setTransactionToDateTime(final LocalDateTime transactionToDateTime) {
        this.transactionToDateTime = transactionToDateTime;
    }
}
