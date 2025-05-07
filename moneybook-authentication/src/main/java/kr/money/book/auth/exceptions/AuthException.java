package kr.money.book.auth.exceptions;

import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Getter
    @AllArgsConstructor
    public enum ErrorCode implements BaseErrorCode {

        INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH_0001", "잘못된 형식의 토큰입니다."),
        TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_0002", "토큰이 만료되었습니다."),
        INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH_0003", "JWT 서명이 유효하지 않습니다."),
        INVALID_3TH_PART_TOKEN(HttpStatus.NOT_FOUND, "AUTH_0004", "잘못된 인증 요청입니다.");

        private final HttpStatus status;
        private final String code;
        private final String message;
    }
}
