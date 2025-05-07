package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "패스워드 수정")
public record PasswordUpdateRequest(

    @Schema(description = "현재 비밀번호")
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String currentPassword,

    @Schema(description = "새로운 비밀번호")
    @NotBlank(message = "새로운 비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String newPassword

) {

}
