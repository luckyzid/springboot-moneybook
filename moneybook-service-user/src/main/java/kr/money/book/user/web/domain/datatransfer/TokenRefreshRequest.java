package kr.money.book.user.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 갱신")
public record TokenRefreshRequest(

    @Schema(description = "Refresh 토큰")
    @NotBlank(message = "Refresh 토큰은 필수입니다.")
    String refreshToken

) {

}
