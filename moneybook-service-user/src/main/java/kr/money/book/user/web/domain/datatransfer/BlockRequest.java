package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 로그인")
public record BlockRequest(

    @Schema(description = "userKey")
    @NotBlank(message = "유저키 값은 필수입니다.")
    String userKey

) {

}
