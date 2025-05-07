package kr.money.book.user.web.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.common.constants.Role;
import lombok.Builder;

@Schema(description = "사용자 로그인")
@Builder(toBuilder = true)
public record UserInfo(

    @Schema(description = "사용자 고유 키")
    String userKey,

    @Schema(description = "제공자")
    String provider,

    @Schema(description = "사용자 이메일")
    String email,

    @Schema(description = "제공자 유니크 키")
    @JsonIgnore
    String uniqueKey,

    @Schema(description = "이메일 로그인 비밀번호")
    @JsonIgnore
    String password,

    @Schema(description = "사용자 이름")
    String name,

    @Schema(description = "사용자 프로필 이미지 URL")
    String profile,

    @Schema(description = "사용자 역할")
    Role role,

    @Schema(description = "접속 아이피")
    String ipAddress,

    @Schema(description = "유저 에이전트")
    String userAgent,

    @Schema(description = "로그인 토큰")
    String token,

    @Schema(description = "차단 여부")
    Boolean isBlocked

) {

}
