package kr.money.book.analyze.web.domain.datatransfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.common.constants.AnalysisType;
import kr.money.book.analyze.web.domain.valueobject.AnalysisInfo;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;

@Schema(description = "분석")
public record AnalysisRequest(

    @Schema(description = "분석 유형: MONTHLY 또는 WEEKLY")
    @NotNull(message = "분석 유형은 필수입니다.")
    AnalysisType analysisType,

    @Schema(description = "분석 시작 날짜")
    @NotNull(message = "시작 날짜는 필수입니다.")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime startDate,

    @Schema(description = "계정 ID 목록 (선택 사항)")
    List<Long> accountIdxList,

    @Schema(description = "카테고리 ID 목록 (선택 사항)")
    List<Long> categoryIdxList

) {

    public AnalysisInfo toAnalysisInfo(String userKey) {

        return AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(analysisType)
            .startDate(startDate)
            .accountIdxList(accountIdxList)
            .categoryIdxList(categoryIdxList)
            .build();
    }
}
