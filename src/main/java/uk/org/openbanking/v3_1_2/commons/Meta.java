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
public class Meta {
    @JsonProperty("TotalPages")
    private int totalPages;
    @JsonProperty("FirstAvailableDateTime")
    private String firstAvailableDateTime;
    @JsonProperty("LastAvailableDateTime")
    private String lastAvailableDateTime;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(final int totalPages) {
        this.totalPages = totalPages;
    }

    public String getFirstAvailableDateTime() {
        return firstAvailableDateTime;
    }

    public void setFirstAvailableDateTime(final String firstAvailableDateTime) {
        this.firstAvailableDateTime = firstAvailableDateTime;
    }

    public String getLastAvailableDateTime() {
        return lastAvailableDateTime;
    }

    public void setLastAvailableDateTime(final String lastAvailableDateTime) {
        this.lastAvailableDateTime = lastAvailableDateTime;
    }
}
