package kr.money.book.analyze.web.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.money.book.analyze.web.domain.entity.BudgetAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetAnalyzeRepository extends JpaRepository<BudgetAnalyze, Long> {

    Optional<BudgetAnalyze> findByIdx(@Param("idx") Long idx);

    List<BudgetAnalyze> findByUserKey(@Param("userKey") String userKey);

    @Query("""
            SELECT 
                b 
            FROM 
                BudgetAnalyze b 
            WHERE 
                b.userKey = :userKey 
                AND b.transactionDate >= :startDate 
                AND b.transactionDate <= :endDate 
                AND (:accountIdxList IS NULL OR b.accountIdx IN :accountIdxList)
                AND (:categoryIdxList IS NULL OR b.categoryIdx IN :categoryIdxList)
        """)
    List<BudgetAnalyze> findByUserKeyAndTransactionDateBetweenAndAccountIdxIn(
        @Param("userKey") String userKey,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("accountIdxList") List<Long> accountIdxList,
        @Param("categoryIdxList") List<Long> categoryIdxList
    );

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
