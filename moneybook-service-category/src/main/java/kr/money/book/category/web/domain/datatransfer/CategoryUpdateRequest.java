package kr.money.book.category.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;

@Schema(description = "카테고리 수정")
public record CategoryUpdateRequest(

    @Schema(description = "카테고리 이름")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
    String name,

    @Schema(description = "상위 카테고리 ID")
    Long parentIdx

) {

    public CategoryInfo toCategoryInfo(String userKey, Long idx) {

        return CategoryInfo.builder()
            .idx(idx)
            .userKey(userKey)
            .name(name)
            .parentIdx(parentIdx)
            .build();
    }
}
