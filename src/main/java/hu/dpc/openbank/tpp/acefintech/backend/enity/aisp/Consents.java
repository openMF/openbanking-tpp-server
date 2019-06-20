package hu.dpc.openbank.tpp.acefintech.backend.enity.aisp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consents {
    @JsonProperty("Permissions")
    public List<String> permissions;
    @JsonProperty("ExpirationDateTime")
    public Date expirationDateTime;
    @JsonProperty("TransactionFromDateTime")
    public Date transactionFromDateTime;
    @JsonProperty("TransactionToDateTime")
    public Date transactionToDateTime;
}
