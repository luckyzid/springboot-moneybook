package kr.money.book.budget.web.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.valueobject.BudgetAmount;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_budget",
    indexes = {
        @Index(name = "idx_mb_budget_user_key", columnList = "user_key"),
        @Index(name = "idx_mb_budget_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_mb_budget_account_idx", columnList = "account_idx"),
        @Index(name = "idx_mb_budget_category_idx", columnList = "category_idx"),
        @Index(name = "idx_mb_budget_update_date", columnList = "update_date")
    }
)
public class Budget extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "user_key", nullable = false, length = 100)
    private String userKey;

    @Column(name = "account_idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long accountIdx;

    @Column(name = "category_idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long categoryIdx;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private BudgetType type;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Builder
    public Budget(String userKey, Long accountIdx, Long categoryIdx, BudgetType type, BigDecimal amount, String comment,
        LocalDateTime transactionDate) {

        this.userKey = userKey;
        this.accountIdx = accountIdx;
        this.categoryIdx = categoryIdx;
        this.type = type;
        this.amount = amount;
        this.comment = comment;
        this.transactionDate = transactionDate;
    }

    public void updateBudget(Long accountIdx, Long categoryIdx, BudgetType type, BigDecimal amount, String comment,
        LocalDateTime transactionDate) {

        this.accountIdx = accountIdx;
        this.categoryIdx = categoryIdx;
        this.type = type;
        this.amount = amount;
        this.comment = comment;
        this.transactionDate = transactionDate;
    }

    public BudgetAmount toBudgetAmount(boolean isSubtract) {

        BigDecimal adjustment = isSubtract ? amount.negate() : amount;
        BigDecimal income = type == BudgetType.INCOME ? adjustment : BigDecimal.ZERO;
        BigDecimal expense = type == BudgetType.EXPENSE ? adjustment : BigDecimal.ZERO;

        return BudgetAmount.builder()
            .userKey(userKey)
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .type(type)
            .transactionDate(transactionDate)
            .amount(adjustment)
            .income(income)
            .expense(expense)
            .build();
    }
}
