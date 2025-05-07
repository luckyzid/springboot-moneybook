package kr.money.book.budget.web.domain.datatransfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.serializer.BigDecimalDeserializer;
import kr.money.book.serializer.BigDecimalSerializer;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;

@Schema(description = "예산 목록 항목")
@Builder(toBuilder = true)
public record BudgetInfoResponse(

    @Schema(description = "예산 idx")
    Long idx,

    @Schema(description = "예산 타입")
    BudgetType type,

    @Schema(description = "금액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal amount,

    @Schema(description = "내역")
    String comment,

    @Schema(description = "거래 발생 시간")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime transactionDate,

    @Schema(description = "계정 idx")
    Long accountIdx,

    @Schema(description = "카테고리 idx")
    Long categoryIdx

) {

    public static BudgetInfoResponse of(BudgetInfo budgetInfo) {

        return BudgetInfoResponse.builder()
            .idx(budgetInfo.idx())
            .type(budgetInfo.type())
            .amount(budgetInfo.amount())
            .comment(budgetInfo.comment())
            .transactionDate(budgetInfo.transactionDate())
            .accountIdx(budgetInfo.accountIdx())
            .categoryIdx(budgetInfo.categoryIdx())
            .build();
    }
}
