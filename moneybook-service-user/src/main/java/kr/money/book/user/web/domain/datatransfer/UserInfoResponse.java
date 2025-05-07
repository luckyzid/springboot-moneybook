package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import lombok.Builder;

@Schema(description = "사용자 정보")
@Builder(toBuilder = true)
public record UserInfoResponse(

    @Schema(description = "사용자 키")
    String userKey,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "이름")
    String name,

    @Schema(description = "OAuth2 제공자 or EMAIL")
    String provider,

    @Schema(description = "역할")
    String role

) {

    public static UserInfoResponse of(UserInfo userInfo) {

        return UserInfoResponse.builder()
            .userKey(userInfo.userKey())
            .email(userInfo.email())
            .name(userInfo.name())
            .provider(userInfo.provider())
            .role(userInfo.role() != null ? userInfo.role().getKey() : null)
            .build();
    }
}
