package kr.money.book.budget.web.domain.datatransfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;

@Schema(description = "예산 생성")
public record BudgetCreateRequest(

    @Schema(description = "예산 타입 (INCOME or EXPENSE)")
    @NotNull(message = "예산 타입은 필수입니다.")
    BudgetType type,

    @Schema(description = "금액")
    @NotNull(message = "금액은 필수입니다.")
    BigDecimal amount,

    @Schema(description = "내역")
    String comment,

    @Schema(description = "거래 발생 시간")
    @NotNull(message = "거래 발생 시간은 필수입니다.")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime transactionDate,

    @Schema(description = "계정 idx")
    @NotNull(message = "계정 idx는 필수입니다.")
    Long accountIdx,

    @Schema(description = "카테고리 idx")
    @NotNull(message = "카테고리 idx는 필수입니다.")
    Long categoryIdx

) {

    public BudgetInfo toBudgetInfo(String userKey) {

        return BudgetInfo.builder()
            .userKey(userKey)
            .type(type)
            .amount(amount)
            .comment(comment)
            .transactionDate(transactionDate)
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();
    }
}
