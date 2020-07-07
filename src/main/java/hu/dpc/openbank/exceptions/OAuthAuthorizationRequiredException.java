/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.exceptions;

import lombok.Getter;

@Getter
public class OAuthAuthorizationRequiredException extends RuntimeException {

  private static final long serialVersionUID = -5483522520337083377L;
  private final String consentId;


  public OAuthAuthorizationRequiredException(final String consentId) {
    super("ConsentID = [" + consentId + "]");
    this.consentId = consentId;
  }
}

