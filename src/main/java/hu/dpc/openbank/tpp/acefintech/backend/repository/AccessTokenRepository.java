package hu.dpc.openbank.tpp.acefintech.backend.repository;

import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, String> {
    /**
     * Get latest user level access token for a desired scope.
     * @param bankId
     * @param userName
     * @param scope
     * @return
     */
    @Query(value = "select *\n" +
            "from (select a.*\n" +
            "      from ACCESS_TOKEN a\n" +
            "      where " +
            "        a.USERNAME = :username\n" +
            "and a.BANK_ID = :bankid\n" +
            "        and a.SCOPE = :scope\n" +
            "        and a.ACCESS_TOKEN_TYPE = 'user'\n" +
            "      order by EXPIRES desc)\n" +
            "limit 1", nativeQuery = true)
    AccessToken getLatest(@Param("bankid") String bankId, @Param("username") String userName, @Param("scope") String scope);
}
