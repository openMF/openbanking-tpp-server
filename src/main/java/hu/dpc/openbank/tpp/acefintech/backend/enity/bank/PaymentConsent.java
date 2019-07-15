/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAYMENT_CONSENT")
public class PaymentConsent {
    @Id
    @Column(name = "BANKID", nullable = false)
    private String bankId;
    @Column(name = "CONSENTID")
    private String consentId;
    @Column(name = "CONSENT_RESPONSE")
    private String consentResponse;
    @Column(name = "DOMESTIC_PAYMENTID")
    private String paymentId;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(final String bankId) {
        this.bankId = bankId;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(final String consentId) {
        this.consentId = consentId;
    }

    public String getConsentResponse() {
        return consentResponse;
    }

    public void setConsentResponse(final String consentResponse) {
        this.consentResponse = consentResponse;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(final String paymentId) {
        this.paymentId = paymentId;
    }
}
