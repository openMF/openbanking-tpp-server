package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACCESS_TOKEN")
public class AccessToken {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "BANK_ID")
    private String bankId;
    @Column(name = "ACCESS_TOKEN")
    private String accessToken;
    @Column(name = "ACCESS_TOKEN_TYPE")
    private String accessTokenType;
    @Column(name = "SCOPE")
    private String scope;
    @Column(name = "EXPRIES")
    private int expires;
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    public AccessToken() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenType() {
        return accessTokenType;
    }

    public void setAccessTokenType(String accessTokenType) {
        this.accessTokenType = accessTokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
