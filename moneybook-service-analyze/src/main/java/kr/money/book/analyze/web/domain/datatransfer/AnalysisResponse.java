package kr.money.book.analyze.web.domain.datatransfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.money.book.common.constants.AnalysisType;
import kr.money.book.analyze.web.domain.valueobject.AnalysisData;
import kr.money.book.analyze.web.domain.valueobject.BudgetAnalysis;
import kr.money.book.analyze.web.domain.valueobject.AnalysisAmount;
import kr.money.book.serializer.BigDecimalDeserializer;
import kr.money.book.serializer.BigDecimalSerializer;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;

@Schema(description = "분석 결과")
@Builder(toBuilder = true)
public record AnalysisResponse(

    @Schema(description = "시작 기간")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime startDate,

    @Schema(description = "종료 기간")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime endDate,

    @Schema(description = "분석 유형: MONTHLY 또는 WEEKLY")
    AnalysisType analysisType,

    @Schema(description = "수입 총액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal totalIncome,

    @Schema(description = "지출 총액")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    BigDecimal totalExpense,

    @Schema(description = "카테고리별 분석")
    Map<String, AnalysisAmount> categoryAnalysis,

    @Schema(description = "계정별 분석")
    Map<String, AnalysisAmount> accountAnalysis,

    @Schema(description = "카테고리별 내역")
    List<BudgetAnalysis> budgets

) {

    public static AnalysisResponse of(AnalysisData analysisData) {

        return AnalysisResponse.builder()
            .startDate(analysisData.startDate())
            .endDate(analysisData.endDate())
            .analysisType(analysisData.analysisType())
            .totalIncome(analysisData.totalIncome())
            .totalExpense(analysisData.totalExpense())
            .categoryAnalysis(analysisData.categoryAnalysis())
            .accountAnalysis(analysisData.accountAnalysis())
            .budgets(analysisData.budgets())
            .build();
    }

}
