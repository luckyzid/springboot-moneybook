package kr.money.book.budget.web.domain.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.entity.BudgetAccount;
import kr.money.book.budget.web.domain.entity.BudgetCategory;
import lombok.Builder;

@Schema(description = "예산 정보")
@Builder(toBuilder = true)
public record BudgetAmount(

    @Schema(description = "유저 키")
    String userKey,

    @Schema(description = "예산 타입")
    BudgetType type,

    @Schema(description = "계정 idx")
    Long accountIdx,

    @Schema(description = "카테고리 idx")
    Long categoryIdx,

    @Schema(description = "거래 발생 시간")
    LocalDateTime transactionDate,

    @Schema(description = "금액")
    BigDecimal amount,

    @Schema(description = "수입")
    BigDecimal income,

    @Schema(description = "지출")
    BigDecimal expense

) {

    public BudgetAccount createBudgetAccountFromAmount() {

        return BudgetAccount.builder()
            .userKey(userKey)
            .accountIdx(accountIdx)
            .transactionDate(transactionDate.toLocalDate())
            .amount(BigDecimal.ZERO)
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.ZERO)
            .build();
    }

    public BudgetCategory createBudgetCategoryFromAmount() {

        return BudgetCategory.builder()
            .userKey(userKey)
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .transactionDate(transactionDate.toLocalDate())
            .amount(BigDecimal.ZERO)
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.ZERO)
            .build();
    }

}
