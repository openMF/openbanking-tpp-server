/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.common.http.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.common.http.HttpResponse;
import lombok.Getter;
import lombok.Setter;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TokenResponse extends HttpResponse {
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
    private long   expiresIn;
    @JsonProperty("error")
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
    @JsonIgnore
    private String subject;
    @JsonIgnore
    private long   jwtExpires;

  
}
