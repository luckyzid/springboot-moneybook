package kr.money.book.batch.entity;

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
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_user_statistics",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_mb_user_statistics_unique", columnNames = {"user_key", "statistics_date"})
    },
    indexes = {
        @Index(name = "idx_mb_user_statistics_user_key", columnList = "user_key"),
        @Index(name = "idx_mb_user_statistics_date", columnList = "statistics_date"),
        @Index(name = "idx_mb_user_statistics_update_date", columnList = "update_date")
    }
)
public class UserStatistics extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "user_key", nullable = false, length = 100)
    private String userKey;

    @Column(name = "statistics_date", nullable = false)
    private LocalDate statisticsDate;

    @Column(name = "total_income", nullable = false)
    private BigDecimal totalIncome;

    @Column(name = "total_expense", nullable = false)
    private BigDecimal totalExpense;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
    
    @Column(name = "process_status", length = 20)
    private String processStatus;

    @Builder
    public UserStatistics(String userKey, LocalDate statisticsDate, BigDecimal totalIncome,
                          BigDecimal totalExpense, BigDecimal balance, String processStatus) {
        this.userKey = userKey;
        this.statisticsDate = statisticsDate;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.processStatus = processStatus;
    }

    public void updateProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
} 