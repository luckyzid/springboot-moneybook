package kr.money.book.auth.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Authentication 토큰 정보")
@Builder(toBuilder = true)
public record AuthToken(

    @Schema(description = "Access 토큰")
    String accessToken,

    @Schema(description = "Refresh 토큰")
    String refreshToken

) {

}

