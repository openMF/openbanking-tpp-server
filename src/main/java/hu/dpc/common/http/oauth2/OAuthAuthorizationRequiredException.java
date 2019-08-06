/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.common.http.oauth2;

public class OAuthAuthorizationRequiredException extends RuntimeException {
    private final String consentId;

    public OAuthAuthorizationRequiredException(final String consentId) {
        super("ConsentID = [" + consentId + "]");
        this.consentId = consentId;
    }

    public String getConsentId() {
        return consentId;
    }
}
