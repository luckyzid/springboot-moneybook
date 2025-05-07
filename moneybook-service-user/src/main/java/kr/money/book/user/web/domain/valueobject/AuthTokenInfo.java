package kr.money.book.user.web.domain.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.auth.valueobject.AuthToken;
import lombok.Builder;

@Schema(description = "토큰 정보")
@Builder(toBuilder = true)
public record AuthTokenInfo(

    @Schema(description = "Access 토큰")
    String accessToken,

    @Schema(description = "Refresh 토큰")
    String refreshToken,

    @Schema(description = "자체 발급 UUID 토큰")
    String userToken

) {

    public static AuthTokenInfo create(AuthToken authToken, String userToken) {

        return AuthTokenInfo.builder()
            .accessToken(authToken.accessToken())
            .refreshToken(authToken.refreshToken())
            .userToken(userToken)
            .build();
    }
}
