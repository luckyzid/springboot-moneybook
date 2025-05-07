package kr.money.book.category.web.exceptions;

import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CategoryException extends RuntimeException {

    private final ErrorCode errorCode;

    public CategoryException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Getter
    @AllArgsConstructor
    public enum ErrorCode implements BaseErrorCode {
        CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_0001", "카테고리를 찾을 수 없습니다."),
        UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "CATEGORY_0002", "권한이 없는 카테고리 접근입니다."),
        INVALID_SYNC_FAILED(HttpStatus.BAD_REQUEST, "CATEGORY_0003", "동기화에 실패했습니다."),
        INVALID_CATEGORY_DEPTH(HttpStatus.BAD_REQUEST, "CATEGORY_0004", "카테고리 계층 깊이가 잘못되었습니다."),
        DUPLICATE_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "CATEGORY_0005", "중복된 카테고리 이름입니다.");

        private final HttpStatus status;
        private final String code;
        private final String message;
    }
}
