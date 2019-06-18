package hu.dpc.openbank.tpp.acefintech.backend.enity.aisp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
    /**
     * <pre>
     * HTTP/1.1 200 OK
     * x-fapi-interaction-id: 93bac548-d2de-4546-b106-880a5018460d
     * Content-Type: application/json
     *
     * {
     *   "Data": {
     *     "Account": [
     *       {
     *         "AccountId": "22289",
     *         "Status": "Enabled",
     *         "StatusUpdateDateTime": "2019-01-01T06:06:06+00:00",
     *         "Currency": "GBP",
     *         "AccountType": "Personal",
     *         "AccountSubType": "CurrentAccount",
     *         "Nickname": "Bills",
     *         "Account": [
     *             {
     *                 "SchemeName": "UK.OBIE.SortCodeAccountNumber",
     *                 "Identification": "80200110203345",
     *                 "Name": "Mr Kevin",
     *                 "SecondaryIdentification": "00021"
     *             }
     *         ]
     *       },
     *       {
     *         "AccountId": "31820",
     *         "Status": "Enabled",
     *         "StatusUpdateDateTime": "2018-01-01T06:06:06+00:00",
     *         "Currency": "GBP",
     *         "AccountType": "Personal",
     *         "AccountSubType": "CurrentAccount",
     *         "Nickname": "Household",
     *         "Account": [
     *             {
     *                 "SchemeName": "UK.OBIE.SortCodeAccountNumber",
     *                 "Identification": "80200110203348",
     *                 "Name": "Mr Kevin"
     *             }
     *         ]
     *       }
     *     ]
     *   },
     *   "Links": {
     *     "Self": "https://api.alphabank.com/open-banking/v3.1/aisp/accounts/"
     *   },
     *   "Meta": {
     *     "TotalPages": 1
     *   }
     * }
     * </pre>
     */

    @JsonProperty("AccountId")
    private String accountId;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("StatusUpdateDateTime")
    private String statusUpdateDateTime;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("AccountSubType")
    private String accountSubType;
    @JsonProperty("Nickname")
    private String nickname;
    @JsonProperty("Account")
    private List<AccountAccount> account;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusUpdateDateTime() {
        return statusUpdateDateTime;
    }

    public void setStatusUpdateDateTime(String statusUpdateDateTime) {
        this.statusUpdateDateTime = statusUpdateDateTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountSubType() {
        return accountSubType;
    }

    public void setAccountSubType(String accountSubType) {
        this.accountSubType = accountSubType;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<AccountAccount> getAccount() {
        return account;
    }

    public void setAccount(List<AccountAccount> account) {
        this.account = account;
    }
}
