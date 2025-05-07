package kr.money.book.account.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import lombok.Builder;

@Schema(description = "계정")
@Builder(toBuilder = true)
public record AccountInfoListResponse(

    @Schema(description = "계정 정보 리스트")
    List<AccountInfoList> lists

) {

    public static AccountInfoListResponse of(List<AccountInfo> accountInfo) {

        return AccountInfoListResponse.builder()
            .lists(accountInfo.stream()
                .filter(Objects::nonNull)
                .map(AccountInfoList::of)
                .collect(Collectors.toList())
            )
            .build();
    }
}
