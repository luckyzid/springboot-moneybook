package kr.money.book.common.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.money.book.common.constants.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "사용자 정보")
public class CacheUser {

    @Schema(description = "사용자 고유 키")
    private String userKey;

    @Schema(description = "제공자")
    private String provider;

    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "제공자 유니크 키")
    private String uniqueKey;

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "사용자 프로필 이미지 URL")
    private String profile;

    @Schema(description = "사용자 역할")
    private Role role;

    @Schema(description = "접속 아이피")
    private String ipAddress;

    @Schema(description = "유저 에이전트")
    private String userAgent;

    @Builder
    public CacheUser(String userKey, String provider, String email, String uniqueKey, String name, String profile,
        Role role, String ipAddress, String userAgent) {
        this.userKey = userKey;
        this.provider = provider;
        this.email = email;
        this.uniqueKey = uniqueKey;
        this.name = name;
        this.profile = profile;
        this.role = role;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}
