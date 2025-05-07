package kr.money.book.category.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import lombok.Builder;

@Schema(description = "카테고리 목록 항목")
@Builder(toBuilder = true)
public record CategoryInfoResponse(

    @Schema(description = "카테고리 고유 키")
    Long idx,

    @Schema(description = "카테고리 이름")
    String name,

    @Schema(description = "상위 카테고리 ID")
    Long parentIdx,

    @Schema(description = "카테고리 깊이")
    int depth

) {

    public static CategoryInfoResponse of(CategoryInfo categoryInfo) {

        return CategoryInfoResponse.builder()
            .idx(categoryInfo.idx())
            .name(categoryInfo.name())
            .parentIdx(categoryInfo.parentIdx())
            .depth(categoryInfo.depth())
            .build();
    }
}
