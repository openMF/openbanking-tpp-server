/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.repository;

import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.PaymentConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentConsentRepository extends JpaRepository<PaymentConsent, String> {
    /**
     * Get Consent ID
     *
     * @param bankId
     * @param consentId
     * @return
     */
    @Query(value = "select *\n" +
            "from PAYMENT_CONSENT \n" +
            "where BANKID = :bankid\n" +
            "  and CONSENTID = :consentId", nativeQuery = true)
    PaymentConsent getConsent(@Param("bankid") String bankId, @Param("consentId") String consentId);

    /**
     * Get Consent ID
     *
     * @param bankId
     * @param consentId
     * @return
     */
    @Query(value = "update PAYMENT_CONSENT \n" +
            "set DOMESTIC_PAYMENTID = :paymentId\n" +
            "where BANKID = :bankid\n" +
            "  and CONSENTID = :consentId", nativeQuery = true)
    PaymentConsent updateConsent(@Param("bankid") String bankId, @Param("consentId") String consentId, @Param("paymentId") String paymentId);

}
