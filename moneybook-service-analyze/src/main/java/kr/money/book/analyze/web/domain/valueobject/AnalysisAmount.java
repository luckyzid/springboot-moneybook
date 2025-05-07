package kr.money.book.analyze.web.domain.valueobject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import kr.money.book.serializer.BigDecimalDeserializer;
import kr.money.book.serializer.BigDecimalSerializer;
import lombok.Builder;

@Schema(description = "분석 정보")
@Builder(toBuilder = true)
public record AnalysisAmount(

    @Schema(description = "금액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal totalAmount,

    @Schema(description = "수입 총액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal totalIncome,

    @Schema(description = "지출 총액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal totalExpense

) {

    public static AnalysisAmount from(BigDecimal totalAmount, BigDecimal totalIncome, BigDecimal totalExpense) {

        return AnalysisAmount.builder()
            .totalAmount(totalAmount)
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .build();
    }

    public static AnalysisAmount merge(AnalysisAmount a1, AnalysisAmount a2) {

        return AnalysisAmount.builder()
            .totalAmount(safeAdd(a1.totalAmount, a2.totalAmount))
            .totalIncome(safeAdd(a1.totalIncome, a2.totalIncome))
            .totalExpense(safeAdd(a1.totalExpense, a2.totalExpense))
            .build();
    }

    private static BigDecimal safeAdd(BigDecimal v1, BigDecimal v2) {
        return (v1 == null ? BigDecimal.ZERO : v1).add(v2 == null ? BigDecimal.ZERO : v2);
    }

}
