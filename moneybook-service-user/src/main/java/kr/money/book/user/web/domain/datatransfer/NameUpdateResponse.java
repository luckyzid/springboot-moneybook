package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import lombok.Builder;

@Schema(description = "이름 수정")
@Builder(toBuilder = true)
public record NameUpdateResponse(

    @Schema(description = "수정된 이름")
    String name

) {

    public static NameUpdateResponse of(UserInfo userInfo) {

        return NameUpdateResponse.builder()
            .name(userInfo.name())
            .build();
    }
}
