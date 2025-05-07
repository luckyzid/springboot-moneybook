package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.money.book.user.web.domain.valueobject.UserInfo;

@Schema(description = "이메일 로그인")
public record EmailLoginRequest(

    @Schema(description = "이메일")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    String email,

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호는 필수입니다.")
    String password

) {

    public UserInfo toUserInfo(String ipAddress, String userAgent) {

        return UserInfo.builder()
            .email(email)
            .password(password)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();
    }
}
