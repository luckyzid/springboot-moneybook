package kr.money.book.budget.web.domain.repository;

import java.util.List;
import java.util.Optional;
import kr.money.book.budget.web.domain.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdx(@Param("idx") Long idx);

    List<Budget> findByUserKey(@Param("userKey") String userKey);

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
