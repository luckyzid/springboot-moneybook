package kr.money.book.analyze.web.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.money.book.analyze.web.domain.entity.BudgetCategoryAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetCategoryAnalyzeRepository extends JpaRepository<BudgetCategoryAnalyze, Long> {

    Optional<BudgetCategoryAnalyze> findByIdx(@Param("idx") Long idx);

    List<BudgetCategoryAnalyze> findByUserKey(@Param("userKey") String userKey);

    @Query("""
        SELECT
            bc 
        FROM 
            BudgetCategoryAnalyze bc
        WHERE
            bc.userKey = :userKey
            AND bc.transactionDate BETWEEN :startDate AND :endDate
        """)
    List<BudgetCategoryAnalyze> findByUserKeyAndTransactionDateBetween(
        @Param("userKey") String userKey,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT
            bc
        FROM
            BudgetCategoryAnalyze bc
        WHERE
            bc.userKey = :userKey
            AND (:accountIdxList IS NULL OR bc.accountIdx IN :accountIdxList)
            AND (:categoryIdxList IS NULL OR bc.categoryIdx IN :categoryIdxList) 
            AND bc.transactionDate BETWEEN :startDate AND :endDate
        """)
    List<BudgetCategoryAnalyze> findByUserKeyAndAccountIdxAndCategoryIdxInAndTransactionDateBetween(
        @Param("userKey") String userKey,
        @Param("accountIdxList") List<Long> accountIdxList,
        @Param("categoryIdxList") List<Long> categoryIdxList,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
