/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BANKS")
public class BankInfo {
    @Id
    @JsonProperty("BankId")
    @Column(name = "ID")
    private String bankId;
    @Column(name = "NAME")
    @JsonProperty("BankName")
    private String bankName;
    @Column(name = "SHORTNAME")
    @JsonProperty("ShortName")
    private String shortName;
    @Column(name = "LONGNAME")
    @JsonProperty("LongName")
    private String longName;
    @Column(name = "LOGOURL")
    @JsonProperty("LogoUrl")
    private String logoUrl;
    @JsonIgnore
    @Column(name = "TOKEN_URL")
    private String tokenUrl;
    @JsonIgnore
    @Column(name = "ACCOUNTS_URL")
    private String accountsUrl;
    @Column(name = "CLIENT_ID")
    @JsonProperty("ClientId")
    private String clientId;
    @JsonIgnore
    @Column(name = "CLIENT_SECRET")
    private String clientSecret;
    @Column(name = "CALLBACK_URL")
    @JsonProperty("CallbackUrl")
    private String callBackUrl;
    @JsonIgnore
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "AUTHORIZE_URL")
    @JsonProperty("AuthorizeUrl")
    private String authorizeUrl;

    public BankInfo() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getAccountsUrl() {
        return accountsUrl;
    }

    public void setAccountsUrl(String accountsUrl) {
        this.accountsUrl = accountsUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }
}
