package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "이름 수정")
public record NameUpdateRequest(

    @Schema(description = "새로운 사용자 이름")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 30자를 초과할 수 없습니다.")
    String name

) {

}
