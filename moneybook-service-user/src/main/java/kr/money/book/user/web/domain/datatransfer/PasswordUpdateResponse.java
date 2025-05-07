package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import lombok.Builder;

@Schema(description = "패스워드 수정")
@Builder(toBuilder = true)
public record PasswordUpdateResponse(

    @Schema(description = "이메일")
    String email

) {

    public static PasswordUpdateResponse of(UserInfo userInfo) {

        return PasswordUpdateResponse.builder()
            .email(userInfo.email())
            .build();
    }
}
