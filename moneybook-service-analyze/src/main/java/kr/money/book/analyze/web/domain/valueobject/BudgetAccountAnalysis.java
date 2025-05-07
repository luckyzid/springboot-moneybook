package kr.money.book.analyze.web.domain.valueobject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import kr.money.book.serializer.BigDecimalDeserializer;
import kr.money.book.serializer.BigDecimalSerializer;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;

@Schema(description = "계정 분석 정보")
@Builder(toBuilder = true)
public record BudgetAccountAnalysis(

    @Schema(description = "계정 ID")
    Long accountIdx,

    @Schema(description = "금액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal amount,

    @Schema(description = "수입")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal income,

    @Schema(description = "지출")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal expense,

    @Schema(description = "거래 발생 시간")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDate transactionDate

) {

}
