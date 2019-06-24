/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <pre>curl -k -X doPost "https://localhost:8243/token"
 *           -H "Content-Type: application/x-www-form-urlencoded"
 *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"
 *           -d "grant_type=client_credentials&scope=accounts openid"</pre>
 * <pre>curl -k -v -X doPost "https://localhost:8243/token?code=cc0970dd-476a-381e-bdd3-84bf69091932&grant_type=authorization_code&redirect_uri=http://acefintech.org/callback&client_id=nYdJa_KHnicVXCYEMNSgKVCiCzwa"
 *           -H "Content-Type: application/x-www-form-urlencoded"
 *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"</pre>
 * <pre>curl -k -v -X doPost "https://localhost:8243/token?refresh_token=e5fd4066-d3de-33cb-a5e5-3b50c36225ec&grant_type=refresh_token&redirect_uri=http://acefintech.org/callback&client_id=nYdJa_KHnicVXCYEMNSgKVCiCzwa"
 *           -H "Content-Type: application/x-www-form-urlencoded"
 *           -H "Authorization: Basic bllkSmFfS0huaWNWWENZRU1OU2dLVkNpQ3p3YTpHWmhXOFlDZkM4VEM0dTJHYVJYU1hsY1JOR3dh"</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("id_token")
    private String idToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("error")
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
    @JsonIgnore
    private String subject;
    @JsonIgnore
    private long jwtExpires;
    @JsonIgnore
    private int httpResponseCode = -1;
    @JsonIgnore
    private String rawContent;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(final String idToken) {
        this.idToken = idToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(final long expiresIn) {
        this.expiresIn = (expiresIn * 1000L) + System.currentTimeMillis();
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(final int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public long getJwtExpires() {
        return jwtExpires;
    }

    public void setJwtExpires(final long jwtExpires) {
        this.jwtExpires = jwtExpires;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(final String rawContent) {
        this.rawContent = rawContent;
    }
}
