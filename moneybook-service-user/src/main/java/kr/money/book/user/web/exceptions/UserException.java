package kr.money.book.user.web.exceptions;

import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {

    private final ErrorCode errorCode;

    public UserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Getter
    @AllArgsConstructor
    public enum ErrorCode implements BaseErrorCode {
        USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_0001", "이미 존재하는 사용자입니다."),
        INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER_0002", "잘못된 인증 정보입니다."),
        INVALID_USER_INFO(HttpStatus.NOT_FOUND, "USER_0003", "사용자 또는 비밀번호 정보가 잘못되었습니다."),
        INVALID_LOGIN_TOKEN(HttpStatus.BAD_REQUEST, "USER_0004", "유효하지 않은 로그인 토큰입니다."),
        NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "USER_0005", "유효하지 않은 로그인 타입입니다."),
        INVALID_TOKEN_CREATE(HttpStatus.BAD_REQUEST, "USER_0006", "토큰 서버에서 에러가 발생했습니다."),
        USER_BLOCKED(HttpStatus.BAD_REQUEST, "USER_0007", "차단된 사용자입니다.");

        private final HttpStatus status;
        private final String code;
        private final String message;
    }
}
