package kr.money.book.analyze.web.domain.valueobject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import kr.money.book.common.constants.AnalysisType;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;

@Schema(description = "분석 정보")
@Builder(toBuilder = true)
public record AnalysisInfo(

    @Schema(description = "유저 키")
    String userKey,

    @Schema(description = "분석 유형: MONTHLY 또는 WEEKLY")
    AnalysisType analysisType,

    @Schema(description = "분석 시작 날짜")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime startDate,

    @Schema(description = "계정 ID 목록 (선택 사항)")
    List<Long> accountIdxList,

    @Schema(description = "카테고리 ID 목록 (선택 사항)")
    List<Long> categoryIdxList

) {

    @Schema(description = "분석 종료 날짜")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime endDate() {

        if (analysisType == AnalysisType.MONTHLY) {
            return startDate.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        }

        return startDate.plusWeeks(1).minusDays(1).with(LocalTime.MAX);
    }

}
