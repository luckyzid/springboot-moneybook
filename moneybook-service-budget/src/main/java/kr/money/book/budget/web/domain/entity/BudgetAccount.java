package kr.money.book.budget.web.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_budget_account",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_mb_budget_account_unique",
            columnNames = {"user_key", "account_idx", "transaction_date"})
    },
    indexes = {
        @Index(name = "idx_mb_budget_account_user_key", columnList = "user_key"),
        @Index(name = "idx_mb_budget_account_account_idx", columnList = "account_idx"),
        @Index(name = "idx_mb_budget_account_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_mb_budget_account_update_date", columnList = "update_date")
    }
)
public class BudgetAccount extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "user_key", nullable = false, length = 100)
    private String userKey;

    @Column(name = "account_idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long accountIdx;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "income", nullable = false, precision = 18, scale = 2)
    private BigDecimal income;

    @Column(name = "expense", nullable = false, precision = 18, scale = 2)
    private BigDecimal expense;

    @Builder
    public BudgetAccount(String userKey, Long accountIdx, LocalDate transactionDate, BigDecimal amount,
        BigDecimal income, BigDecimal expense) {

        this.userKey = userKey;
        this.accountIdx = accountIdx;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.income = income;
        this.expense = expense;
    }

    public void adjustAmount(BigDecimal amountAdjustment, BigDecimal incomeAdjustment, BigDecimal expenseAdjustment) {
        // BudgetType type
        // BigDecimal adjustedAmount = (type == BudgetType.EXPENSE) ? amountAdjustment.negate() : amountAdjustment;

        this.amount = this.amount.add(amountAdjustment);
        this.income = this.income.add(incomeAdjustment);
        this.expense = this.expense.add(expenseAdjustment);
    }
}
