package kr.money.book.analyze.web.domain.valueobject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.money.book.serializer.BigDecimalDeserializer;
import kr.money.book.serializer.BigDecimalSerializer;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;

@Schema(description = "예산 분석 정보")
@Builder(toBuilder = true)
public record BudgetAnalysis(

    @Schema(description = "예산 타입 (INCOME/EXPENSE)")
    String type,

    @Schema(description = "내역")
    String comment,

    @Schema(description = "계정 ID")
    Long accountIdx,

    @Schema(description = "카테고리 ID")
    Long categoryIdx,

    @Schema(description = "금액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal amount,

    @Schema(description = "거래 발생 시간")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime transactionDate

) {

}
