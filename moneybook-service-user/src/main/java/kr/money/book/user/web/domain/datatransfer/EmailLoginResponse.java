package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import lombok.Builder;

@Schema(description = "이메일 로그인")
@Builder(toBuilder = true)
public record EmailLoginResponse(

    @Schema(description = "Access 토큰")
    String accessToken,

    @Schema(description = "Refresh 토큰")
    String refreshToken,

    @Schema(description = "자체 발급 토큰")
    String userToken

) {

    public static EmailLoginResponse of(AuthTokenInfo authTokenInfo) {

        return EmailLoginResponse.builder()
            .accessToken(authTokenInfo.accessToken())
            .refreshToken(authTokenInfo.refreshToken())
            .userToken(authTokenInfo.userToken())
            .build();
    }
}

