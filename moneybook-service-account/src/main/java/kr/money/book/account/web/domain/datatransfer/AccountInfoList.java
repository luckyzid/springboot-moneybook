package kr.money.book.account.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.common.constants.AccountType;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import lombok.Builder;

@Schema(description = "계정")
@Builder(toBuilder = true)
public record AccountInfoList(

    @Schema(description = "계정 고유 키")
    Long idx,

    @Schema(description = "계정 이름")
    String name,

    @Schema(description = "계정 타입")
    AccountType type

) {

    public static AccountInfoList of(AccountInfo accountInfo) {

        return AccountInfoList.builder()
            .idx(accountInfo.idx())
            .name(accountInfo.name())
            .type(accountInfo.type())
            .build();
    }
}
