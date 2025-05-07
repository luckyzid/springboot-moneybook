package kr.money.book.category.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import lombok.Builder;

@Schema(description = "카테고리 목록")
@Builder(toBuilder = true)
public record CategoryInfoListResponse(

    @Schema(description = "카테고리 정보 리스트")
    List<CategoryInfoList> lists

) {

    public static CategoryInfoListResponse of(List<CategoryInfo> categoryInfos) {

        return CategoryInfoListResponse.builder()
            .lists(categoryInfos.stream()
                .filter(Objects::nonNull)
                .map(CategoryInfoList::of)
                .collect(Collectors.toList())
            )
            .build();
    }
}
