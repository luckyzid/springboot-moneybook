package kr.money.book.shorturl.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import kr.money.book.web.response.GlobalApiResponse;
import kr.money.book.web.response.GlobalErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ShortUrlExceptionHandler {

    @ExceptionHandler(ShortUrlException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleUserException(
        ShortUrlException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(ex.getErrorCode(), request.getRequestURI());

        return ResponseEntity
            .status(ex.getErrorCode().getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }
}
