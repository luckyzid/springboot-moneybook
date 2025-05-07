package kr.money.book.common.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "카테고리 정보")
public class CacheCategory {

    @Schema(description = "카테고리 고유 키")
    private Long idx;

    @Schema(description = "카테고리 이름")
    private String name;

    @Schema(description = "상위 카테고리 ID")
    @JsonProperty("parent_idx")
    private Long parentIdx;

    @Schema(description = "계층 깊이")
    private Integer depth;

    @Builder
    public CacheCategory(Long idx, String name, Long parentIdx, Integer depth) {
        this.idx = idx;
        this.name = name;
        this.parentIdx = parentIdx;
        this.depth = depth;
    }
}
