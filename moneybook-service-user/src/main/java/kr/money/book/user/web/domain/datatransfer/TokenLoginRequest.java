package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.money.book.user.web.domain.valueobject.UserInfo;

@Schema(description = "토큰 로그인")
public record TokenLoginRequest(

    @Schema(description = "userToken")
    @NotBlank(message = "유저 토큰값은 필수입니다.")
    String token

) {

    public UserInfo toUserInfo(String ipAddress, String userAgent) {

        return UserInfo.builder()
            .token(token)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();
    }
}
