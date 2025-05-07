package kr.money.book.web.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import kr.money.book.web.response.GlobalApiResponse;
import kr.money.book.web.response.GlobalErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleIllegalArgumentException(
        IllegalArgumentException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.RESOURCE_NOT_FOUND,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalApiResponse<String>> handleRuntimeException(
        RuntimeException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(GlobalApiResponse.error(globalErrorResponse));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponse<?>> handleGeneralException(
        Exception ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleBindException(
        BindException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleForbiddenException(
        ForbiddenException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleUnauthorizedException(
        UnauthorizedException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleBadRequestException(
        BadRequestException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleResourceNotFoundException(
        ResourceNotFoundException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INVALID_INPUT_VALUE,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleNullPointerExceptionHandler(
        NullPointerException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(GlobalApiResponse.error(globalErrorResponse));
    }
}
