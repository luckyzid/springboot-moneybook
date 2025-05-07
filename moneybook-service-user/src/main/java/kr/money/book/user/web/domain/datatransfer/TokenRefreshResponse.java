package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import lombok.Builder;

@Schema(description = "토큰 갱신")
@Builder(toBuilder = true)
public record TokenRefreshResponse(

    @Schema(description = "새로운 Access 토큰")
    String accessToken,

    @Schema(description = "새로운 Refresh 토큰")
    String refreshToken,

    @Schema(description = "이미 발급되어 있거나 최신 user 토큰")
    String userToken

) {

    public static TokenRefreshResponse of(AuthTokenInfo authTokenInfo) {

        return TokenRefreshResponse.builder()
            .accessToken(authTokenInfo.accessToken())
            .refreshToken(authTokenInfo.refreshToken())
            .userToken(authTokenInfo.userToken())
            .build();
    }
}
