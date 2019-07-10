/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

/*
 * Payment Initiation API
 * Swagger for Payment Initiation API Specification
 *
 * OpenAPI spec version: v3.1.2
 * Contact: ServiceDesk@openbanking.org.uk
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package uk.org.openbanking.v3_1_2.payments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Links relevant to the payload
 */
@ApiModel(description = "Links relevant to the payload")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-09T18:27:25.589536+02:00[Europe/Budapest]")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Links {
    @JsonProperty("Self")
    private String self = null;

    @JsonProperty("First")
    private String first = null;

    @JsonProperty("Prev")
    private String prev = null;

    @JsonProperty("Next")
    private String next = null;

    @JsonProperty("Last")
    private String last = null;

    public Links self(String self) {
        this.self = self;
        return this;
    }

    /**
     * Get self
     *
     * @return self
     **/
    @JsonProperty("Self")
    @ApiModelProperty(required = true, value = "")
    @NotNull
    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public Links first(String first) {
        this.first = first;
        return this;
    }

    /**
     * Get first
     *
     * @return first
     **/
    @JsonProperty("First")
    @ApiModelProperty(value = "")
    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public Links prev(String prev) {
        this.prev = prev;
        return this;
    }

    /**
     * Get prev
     *
     * @return prev
     **/
    @JsonProperty("Prev")
    @ApiModelProperty(value = "")
    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public Links next(String next) {
        this.next = next;
        return this;
    }

    /**
     * Get next
     *
     * @return next
     **/
    @JsonProperty("Next")
    @ApiModelProperty(value = "")
    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Links last(String last) {
        this.last = last;
        return this;
    }

    /**
     * Get last
     *
     * @return last
     **/
    @JsonProperty("Last")
    @ApiModelProperty(value = "")
    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    @Override
    public int hashCode() {
        return Objects.hash(self, first, prev, next, last);
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Links links = (Links) o;
        return Objects.equals(this.self, links.self) &&
                Objects.equals(this.first, links.first) &&
                Objects.equals(this.prev, links.prev) &&
                Objects.equals(this.next, links.next) &&
                Objects.equals(this.last, links.last);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Links {\n");

        sb.append("    self: ").append(toIndentedString(self)).append("\n");
        sb.append("    first: ").append(toIndentedString(first)).append("\n");
        sb.append("    prev: ").append(toIndentedString(prev)).append("\n");
        sb.append("    next: ").append(toIndentedString(next)).append("\n");
        sb.append("    last: ").append(toIndentedString(last)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
