package kr.money.book.analyze.web.domain.valueobject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.money.book.common.constants.AnalysisType;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;

@Schema(description = "분석 데이터")
@Builder(toBuilder = true)
public record AnalysisData(

    @Schema(description = "시작 날짜")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime startDate,

    @Schema(description = "종료 날짜")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime endDate,

    @Schema(description = "분석 유형")
    AnalysisType analysisType,

    @Schema(description = "계정 ID 목록 (선택 사항)")
    List<Long> accountIdxList,

    @Schema(description = "카테고리 ID 목록 (선택 사항)")
    List<Long> categoryIdxList,

    @Schema(description = "총 수입")
    BigDecimal totalIncome,

    @Schema(description = "총 지출")
    BigDecimal totalExpense,

    @Schema(description = "카테고리별 분석")
    Map<String, AnalysisAmount> categoryAnalysis,

    @Schema(description = "계정별 분석")
    Map<String, AnalysisAmount> accountAnalysis,

    @Schema(description = "예산 내역")
    List<BudgetAnalysis> budgets

) {

}
