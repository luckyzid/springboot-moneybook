package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import lombok.Builder;

@Schema(description = "사용자 회원가입")
@Builder(toBuilder = true)
public record EmailCreateResponse(

    @Schema(description = "가입된 사용자 키")
    String userKey,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "이름")
    String name,

    @Schema(description = "로그인 제공자 정보")
    String provider

) {

    public static EmailCreateResponse of(UserInfo userInfo) {

        return EmailCreateResponse.builder()
            .userKey(userInfo.userKey())
            .email(userInfo.email())
            .name(userInfo.name())
            .provider(userInfo.provider())
            .build();
    }
}
