package kr.money.book.shorturl.web.domain.datatransfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import lombok.Builder;

@Schema(description = "단축 URL 목록 항목")
@Builder(toBuilder = true)
public record ShortUrlInfoList(

    @Schema(description = "단축 키")
    String shortKey,

    @Schema(description = "원본 URL")
    String originalUrl,

    @Schema(description = "만료 날짜")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime expireDate,

    @Schema(description = "서버 도메인")
    String shortUrlDomain

) {

    public static ShortUrlInfoList of(ShortUrlInfo shortUrlInfo) {

        return ShortUrlInfoList.builder()
            .shortKey(shortUrlInfo.shortKey())
            .originalUrl(shortUrlInfo.originalUrl())
            .expireDate(shortUrlInfo.expireDate())
            .shortUrlDomain(shortUrlInfo.shortUrlDomain())
            .build();
    }
}
