package kr.money.book.account.web.exceptions;

import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountException extends RuntimeException {

    private final ErrorCode errorCode;

    public AccountException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Getter
    @AllArgsConstructor
    public enum ErrorCode implements BaseErrorCode {
        ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_0001", "계정을 찾을 수 없습니다."),
        UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "ACCOUNT_0002", "권한이 없는 계정 접근입니다."),
        INVALID_SYNC_FAILED(HttpStatus.BAD_REQUEST, "ACCOUNT_0003", "동기화에 실패했습니다.");

        private final HttpStatus status;
        private final String code;
        private final String message;
    }
}
