package kr.money.book.shorturl.web.domain.datatransfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;

@Schema(description = "단축 URL 생성")
public record ShortUrlCreateRequest(

    @Schema(description = "원본 URL")
    @NotBlank(message = "원본 URL은 필수입니다.")
    String originalUrl,

    @Schema(description = "새로운 만료 날짜")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime expireDate

) {

    public ShortUrlInfo toShortUrlInfo() {

        return ShortUrlInfo.builder()
            .originalUrl(originalUrl)
            .expireDate(expireDate)
            .build();
    }

}
