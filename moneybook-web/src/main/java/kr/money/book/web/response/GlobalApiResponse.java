package kr.money.book.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "api 기본")
@Getter
@NoArgsConstructor
public class GlobalApiResponse<T> {

    @Schema(description = "성공여부", defaultValue = "true", allowableValues = {"true", "false"})
    private boolean success;
    @Schema(description = "데이터")
    private T data;
    @Schema(description = "에러정보")
    private GlobalErrorResponse error;

    public static <T> GlobalApiResponse<T> success(T data) {
        GlobalApiResponse<T> response = new GlobalApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> GlobalApiResponse<T> error(GlobalErrorResponse globalErrorResponse) {
        GlobalApiResponse<T> response = new GlobalApiResponse<>();
        response.success = false;
        response.error = globalErrorResponse;
        return response;
    }
}
