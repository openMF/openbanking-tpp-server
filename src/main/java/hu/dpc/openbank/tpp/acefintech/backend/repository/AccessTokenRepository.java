/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.repository;

import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, String> {
    /**
     * Get latest user level access token for a desired scope.
     * @param bankId
     * @param userName
     * @param scope
     * @return
     */
    @Query(value = "select a.*\n" + "      from ACCESS_TOKEN a\n" + "      where a.USERNAME = :username\n" + "        and a.BANK_ID = :bankid\n" + "        and a.SCOPE = :scope\n" + "        and a.ACCESS_TOKEN_TYPE = 'user'", nativeQuery = true)
    AccessToken getLatest(@Param("bankid") String bankId, @Param("username") String userName, @Param("scope") String scope);

    /**
     * Remove user level access token for a desired scope.
     *
     * @param bankId
     * @param userName
     * @param scope
     * @return
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from ACCESS_TOKEN a\n" + "      where USERNAME = :username\n" + "        and BANK_ID = :bankid\n" + "        and SCOPE = :scope\n" + "        and ACCESS_TOKEN_TYPE = 'user'", nativeQuery = true)
    void remove(@Param("bankid") String bankId, @Param("username") String userName, @Param("scope") String scope);
}
