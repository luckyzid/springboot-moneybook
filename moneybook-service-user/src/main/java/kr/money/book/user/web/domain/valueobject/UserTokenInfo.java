package kr.money.book.user.web.domain.valueobject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import kr.money.book.serializer.LocalDateTimeDeserializer;
import kr.money.book.serializer.LocalDateTimeSerializer;
import lombok.Builder;

@Schema(description = "사용자 토큰")
@Builder(toBuilder = true)
public record UserTokenInfo(

    @Schema(description = "토큰 고유 키")
    Long idx,

    @Schema(description = "사용자 정보")
    UserInfo userInfo,

    @Schema(description = "토큰 값")
    String token,

    @Schema(description = "활성 상태 (1: 활성, 0: 비활성)")
    int isActive,

    @Schema(description = "디바이스 정보")
    String deviceInfo,

    @Schema(description = "IP 주소")
    String ipAddress,

    @Schema(description = "만료 시간")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime expirationTime

) {

    public boolean isExpired() {

        return this.expirationTime.isBefore(LocalDateTime.now());
    }

    public boolean invalidActiveToken() {

        return isExpired() || this.isActive == 0;
    }

    public boolean matchesDeviceAndIp(String deviceInfo, String ipAddress) {

        return this.deviceInfo.equals(deviceInfo) || this.ipAddress.equals(ipAddress);
    }
}
