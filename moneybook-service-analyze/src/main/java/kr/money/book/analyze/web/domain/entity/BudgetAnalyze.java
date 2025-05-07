package kr.money.book.analyze.web.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mb_budget")
public class BudgetAnalyze extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "user_key", nullable = false, length = 100)
    private String userKey;

    @Column(name = "account_idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long accountIdx;

    @Column(name = "category_idx", nullable = false, columnDefinition = "BIGINT UNSIGNED") // 추가
    private Long categoryIdx;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Builder
    public BudgetAnalyze(Long idx, String userKey, Long accountIdx, Long categoryIdx, String type, BigDecimal amount,
        String comment,
        LocalDateTime transactionDate) {

        this.idx = idx;
        this.userKey = userKey;
        this.accountIdx = accountIdx;
        this.categoryIdx = categoryIdx;
        this.type = type;
        this.amount = amount;
        this.comment = comment;
        this.transactionDate = transactionDate;
    }
}
