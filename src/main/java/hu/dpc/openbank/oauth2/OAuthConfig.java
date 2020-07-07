/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.oauth2;

import hu.dpc.openbank.tpp.acefintech.backend.entity.bank.BankInfo;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Getter;

@Getter
public class OAuthConfig {

  /**
   * OAuth token url
   */
  private final URL tokenURL;
  private final BankInfo bankInfo;


  public OAuthConfig(final BankInfo bankInfo) throws MalformedURLException {
    this.bankInfo = bankInfo;
    tokenURL = new URL(bankInfo.getTokenUrl());
  }


  /**
   * OAuth client id (consumer key)
   */
  public String getApiKey() {
    return bankInfo.getClientId();
  }


  /**
   * OAuth client secret (consumer secret)
   */
  public String getApiSecret() {
    return bankInfo.getClientSecret();
  }


  /**
   * OAuth callback url
   */
  public String getCallbackURL() {
    return bankInfo.getCallBackUrl();
  }
}

