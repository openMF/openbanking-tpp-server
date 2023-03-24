/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.oauth2;

import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class OAuthConfig {
    private String apiKey;
    private String apiSecret;
    private String callbackURL;
    private URL tokenURL;
    private String subject;
    private BankInfo bankInfo;

    public OAuthConfig(final BankInfo bankInfo) throws MalformedURLException {
        this.bankInfo = bankInfo;
        apiKey = bankInfo.getClientId();
        apiSecret = bankInfo.getClientSecret();
        callbackURL = bankInfo.getCallBackUrl();
        tokenURL = new URL(bankInfo.getTokenUrl());
    }

    public OAuthConfig() {
    }

   
}
