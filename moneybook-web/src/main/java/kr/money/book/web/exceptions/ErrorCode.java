package kr.money.book.web.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {
    INVALID_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_9999", "서버 에러가 발생했습니다."),
    INVALID_FORBIDDEN(HttpStatus.FORBIDDEN, "SERVER_0000", "잘못된 요청 값입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "SERVER_0001", "잘못된 입력 값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "SERVER_0002", "접근이 거부되었습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVER_0003", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_0004", "서버 에러가 발생했습니다."),
    INVALID_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SERVER_0005", "권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
