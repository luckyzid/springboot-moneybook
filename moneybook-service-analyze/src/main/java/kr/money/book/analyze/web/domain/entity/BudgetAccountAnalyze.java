package kr.money.book.analyze.web.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mb_budget_account")
public class BudgetAccountAnalyze extends MutableBaseEntity {

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
    public BudgetAccountAnalyze(Long idx, String userKey, Long accountIdx, LocalDate transactionDate,
        BigDecimal amount, BigDecimal income, BigDecimal expense) {

        this.idx = idx;
        this.userKey = userKey;
        this.accountIdx = accountIdx;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.income = income;
        this.expense = expense;
    }
}
