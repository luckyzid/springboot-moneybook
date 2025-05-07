package kr.money.book.category.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import lombok.Builder;

@Schema(description = "카테고리 생성")
@Builder(toBuilder = true)
public record CategoryCreateResponse(

    @Schema(description = "카테고리 고유 키")
    Long idx,

    @Schema(description = "카테고리 이름")
    String name,

    @Schema(description = "상위 카테고리 ID")
    Long parentIdx,

    @Schema(description = "카테고리 깊이")
    int depth,

    String userKey

) {

    public static CategoryCreateResponse of(CategoryInfo categoryInfo) {

        return CategoryCreateResponse.builder()
            .idx(categoryInfo.idx())
            .name(categoryInfo.name())
            .parentIdx(categoryInfo.parentIdx())
            .depth(categoryInfo.depth())
            .userKey(categoryInfo.userKey())
            .build();
    }
}
