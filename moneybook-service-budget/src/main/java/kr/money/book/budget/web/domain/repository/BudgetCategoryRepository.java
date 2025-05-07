package kr.money.book.budget.web.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import kr.money.book.budget.web.domain.entity.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {

    Optional<BudgetCategory> findByIdx(@Param("idx") Long idx);

    Optional<BudgetCategory> findByUserKeyAndAccountIdxAndCategoryIdxAndTransactionDate(
        @Param("userKey") String userKey,
        @Param("accountIdx") Long accountIdx,
        @Param("categoryIdx") Long categoryIdx,
        @Param("transactionDate") LocalDate transactionDate
    );

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
