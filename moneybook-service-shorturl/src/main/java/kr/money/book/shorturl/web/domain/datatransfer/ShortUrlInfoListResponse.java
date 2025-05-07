package kr.money.book.shorturl.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import lombok.Builder;

@Schema(description = "단축 URL 목록 응답")
@Builder(toBuilder = true)
public record ShortUrlInfoListResponse(

    @Schema(description = "단축 URL 정보 리스트")
    List<ShortUrlInfoList> lists

) {

    public static ShortUrlInfoListResponse of(List<ShortUrlInfo> shortUrlInfos) {
        return ShortUrlInfoListResponse.builder()
            .lists(shortUrlInfos.stream()
                .filter(Objects::nonNull)
                .map(ShortUrlInfoList::of)
                .collect(Collectors.toList())
            )
            .build();
    }
}
