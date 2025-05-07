package kr.money.book.analyze.web.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.money.book.analyze.web.domain.entity.BudgetAccountAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetAccountAnalyzeRepository extends JpaRepository<BudgetAccountAnalyze, Long> {

    Optional<BudgetAccountAnalyze> findByIdx(@Param("idx") Long idx);

    List<BudgetAccountAnalyze> findByUserKey(@Param("userKey") String userKey);

    @Query("""
        SELECT
            ba
        FROM
            BudgetAccountAnalyze ba
        WHERE
            ba.userKey = :userKey 
            AND (:accountIdxList IS NULL OR ba.accountIdx IN :accountIdxList)
            AND ba.transactionDate BETWEEN :startDate AND :endDate
        """)
    List<BudgetAccountAnalyze> findByUserKeyAndAccountIdxInAndTransactionDateBetween(
        @Param("userKey") String userKey,
        @Param("accountIdxList") List<Long> accountIdxList,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
