package kr.money.book.shorturl.web.domain.valueobject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;

@Schema(description = "단축 URL 정보")
@Builder(toBuilder = true)
public record ShortUrlInfo(

    @Schema(description = "단축 키")
    String shortKey,

    @Schema(description = "원본 URL")
    String originalUrl,

    @Schema(description = "만료 날짜")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime expireDate

) {

    @Value("${short.url.domain}")
    private static String shortUrlDomain;

    public String shortUrlDomain() {
        return shortUrlDomain;
    }
}
