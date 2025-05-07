package kr.money.book.shorturl.web.exception;

import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ShortUrlException extends RuntimeException {

    private final ErrorCode errorCode;

    public ShortUrlException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Getter
    @AllArgsConstructor
    public enum ErrorCode implements BaseErrorCode {
        INVALID_URL(HttpStatus.BAD_REQUEST, "SHORTURL_0001", "유효하지 않은 단축 URL입니다."),
        CREATED_FAILED(HttpStatus.BAD_REQUEST, "SHORTURL_0002", "생성에 실패했습니다. 다시 시도해주세요.");

        private final HttpStatus status;
        private final String code;
        private final String message;
    }
}
