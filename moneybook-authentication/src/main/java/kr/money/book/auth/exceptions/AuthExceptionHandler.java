package kr.money.book.auth.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import kr.money.book.web.response.GlobalApiResponse;
import kr.money.book.web.response.GlobalErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleTokenException(
        AuthException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(ex.getErrorCode(), request.getRequestURI());

        return ResponseEntity
            .status(ex.getErrorCode().getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }
}
