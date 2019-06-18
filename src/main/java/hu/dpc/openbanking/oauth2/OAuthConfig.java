package hu.dpc.openbanking.oauth2;

import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;

import java.net.MalformedURLException;
import java.net.URL;

public class OAuthConfig {
    private String apiKey;
    private String apiSecret;
    private String callbackURL;
    private URL tokenURL;
    private String subject;

    public OAuthConfig(BankInfo bankInfo) throws MalformedURLException {
        apiKey = bankInfo.getClientId();
        apiSecret = bankInfo.getClientSecret();
        callbackURL = bankInfo.getCallBackUrl();
        tokenURL = new URL(bankInfo.getTokenUrl());
    }

    public OAuthConfig() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public URL getTokenURL() {
        return tokenURL;
    }

    public void setTokenURL(String tokenURL) throws MalformedURLException {
        this.tokenURL = new URL(tokenURL);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }
}
