package kr.money.book.common.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Schema(description = "계정 정보")
public class CacheAccount {

    @Schema(description = "계정 고유 키")
    private Long idx;

    @Schema(description = "계정 이름")
    private String name;

    @Schema(description = "계정 타입")
    private String type;

    @Builder
    public CacheAccount(Long idx, String name, String type) {
        this.idx = idx;
        this.name = name;
        this.type = type;
    }
}
