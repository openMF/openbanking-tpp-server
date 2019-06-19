package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USER_BANK")
public class UserBank {
    @Id
    @Column(name="ID")
    private int id;
    @Column(name = "USERNAME")
    private String userName;
    @Column(name = "BANK_ID")
    private String bankId;
    @Column(name = "CODE")
    private String code;
    @Column(name = "BANK_USERNAME")
    private String bankUserName;
    @Column(name = "ACCESS_TOKEN")
    private String accessToken;
    @Column(name = "SCOPE")
    private String scope;

    public UserBank() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBankUserName() {
        return bankUserName;
    }

    public void setBankUserName(String bankUserName) {
        this.bankUserName = bankUserName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
