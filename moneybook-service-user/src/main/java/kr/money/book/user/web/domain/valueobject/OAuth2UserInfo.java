package kr.money.book.user.web.domain.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "OAuth2 사용자 정보")
@Builder(toBuilder = true)
public record OAuth2UserInfo(

    @Schema(description = "이름")
    String name,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "프로파일 정보")
    String profile,

    @Schema(description = "제공자")
    String provider,

    @Schema(description = "제공자 유니크 키")
    String uniqueKey

) {

}
