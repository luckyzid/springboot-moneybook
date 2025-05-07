package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.money.book.user.web.domain.valueobject.UserInfo;

@Schema(description = "사용자 회원가입")
public record EmailCreateRequest(

    @Schema(description = "사용자 이메일")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    String email,

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password,

    @Schema(description = "이름")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 30자를 초과할 수 없습니다.")
    String name

) {

    public UserInfo toUserInfo() {

        return UserInfo.builder()
            .email(email)
            .password(password)
            .name(name)
            .build();
    }
}
