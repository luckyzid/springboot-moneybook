package kr.money.book.category.web.domain.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "카테고리 정보")
@Builder(toBuilder = true)
public record CategoryInfo(

    @Schema(description = "카테고리 idx")
    Long idx,

    @Schema(description = "유저 키")
    String userKey,

    @Schema(description = "카테고리 이름")
    String name,

    @Schema(description = "상위 카테고리 ID")
    Long parentIdx,

    @Schema(description = "카테고리 깊이")
    int depth

) {

    public int getDepth() {
        return depth;
    }
}
