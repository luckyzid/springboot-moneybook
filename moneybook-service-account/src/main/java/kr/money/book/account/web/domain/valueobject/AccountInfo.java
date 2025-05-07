package kr.money.book.account.web.domain.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.common.constants.AccountType;
import lombok.Builder;

@Schema(description = "사용자 로그인")
@Builder(toBuilder = true)
public record AccountInfo(
    @Schema(description = "계정 idx")
    Long idx,
    @Schema(description = "유저 키")
    String userKey,
    @Schema(description = "계정 이름")
    String name,
    @Schema(description = "계정 타입")
    AccountType type
) {

}
