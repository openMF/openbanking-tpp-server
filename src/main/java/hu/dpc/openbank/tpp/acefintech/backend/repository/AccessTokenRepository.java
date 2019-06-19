package hu.dpc.openbank.tpp.acefintech.backend.repository;

import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, String> {
    /**
     * Get latest user level access token for a desired scope.
     * @param bankId
     * @param userId
     * @param scope
     * @return
     */
    @Query(value = "select *\n" +
            "from (select a.*\n" +
            "      from ACCESS_TOKEN a\n" +
            "      where a.BANK_ID = ?1\n" +
            "        and a.USERNAME = ?2\n" +
            "        and a.SCOPE = ?3\n" +
            "        and a.ACCESS_TOKEN_TYPE = 'user'\n" +
            "      order by EXPIRES desc)\n" +
            "limit 1", nativeQuery = true)
    AccessToken getLatest(String bankId, String userId, String scope);

    /**
     * Get latest client (tpp) level access token for a desired scope.
     * @param bankId
     * @param scope
     * @return
     */
    @Query(value = "select *\n" +
            "from (select a.*\n" +
            "      from ACCESS_TOKEN a\n" +
            "      where a.BANK_ID = ?1\n" +
            "        and a.SCOPE = ?2\n" +
            "        and a.ACCESS_TOKEN_TYPE = 'client'\n" +
            "      order by EXPIRES desc)\n" +
            "limit 1", nativeQuery = true)
    AccessToken getLatest(String bankId, String scope);
}
