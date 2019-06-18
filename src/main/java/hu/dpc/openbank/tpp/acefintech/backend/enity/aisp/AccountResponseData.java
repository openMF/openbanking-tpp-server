package hu.dpc.openbank.tpp.acefintech.backend.enity.aisp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseData {
    @JsonProperty("Data")
    private AccountResponse response;

    public AccountResponse getResponse() {
        return response;
    }

    public void setResponse(AccountResponse response) {
        this.response = response;
    }
}
