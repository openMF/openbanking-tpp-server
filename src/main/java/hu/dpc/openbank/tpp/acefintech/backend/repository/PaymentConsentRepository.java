/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.repository;

import hu.dpc.openbank.tpp.acefintech.backend.entity.bank.PaymentConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PaymentConsentRepository extends JpaRepository<PaymentConsent, String> {

  /**
   * Get Consent ID
   */
  @Transactional(readOnly = true)
  @Query(value = "select *\n" +
      "from PAYMENT_CONSENT \n" +
      "where BANKID = :bankid\n" +
      "  and CONSENTID = :consentId", nativeQuery = true)
  PaymentConsent getConsent(@Param("bankid") String bankId, @Param("consentId") String consentId);
}
