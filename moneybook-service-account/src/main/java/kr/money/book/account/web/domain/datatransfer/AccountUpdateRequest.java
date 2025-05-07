package kr.money.book.account.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.money.book.common.constants.AccountType;
import kr.money.book.account.web.domain.valueobject.AccountInfo;

@Schema(description = "계정 생성")
public record AccountUpdateRequest(

    @Schema(description = "계정 이름")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "생성 타입은 30자를 초과할 수 없습니다.")
    String name,

    @Schema(description = "계정 타입(BANK or CARD)")
    @NotNull(message = "생성 타입은 필수입니다.")
    AccountType type

) {

    public AccountInfo toAccountInfo(String userKey, Long idx) {

        return AccountInfo.builder()
            .idx(idx)
            .userKey(userKey)
            .name(name)
            .type(type)
            .build();
    }

}
