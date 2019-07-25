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
public class InteropTransactionType {
    @JsonProperty("scenario")
    private String scenario;
    @JsonProperty("initiator")
    private String initiator;
    @JsonProperty("initiatorType")
    private String initiatorType;

    public String getScenario() {
        return scenario;
    }

    public void setScenario(final String scenario) {
        this.scenario = scenario;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(final String initiator) {
        this.initiator = initiator;
    }

    public String getInitiatorType() {
        return initiatorType;
    }

    public void setInitiatorType(final String initiatorType) {
        this.initiatorType = initiatorType;
    }
}
