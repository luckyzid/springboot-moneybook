package kr.money.book.budget.web.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import kr.money.book.budget.web.domain.entity.BudgetAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetAccountRepository extends JpaRepository<BudgetAccount, Long> {

    Optional<BudgetAccount> findByIdx(@Param("idx") Long idx);

    Optional<BudgetAccount> findByUserKeyAndAccountIdxAndTransactionDate(
        @Param("userKey") String userKey,
        @Param("accountIdx") Long accountIdx,
        @Param("transactionDate") LocalDate transactionDate
    );

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
