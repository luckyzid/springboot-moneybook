package kr.money.book.analyze.web.exceptions;

import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AnalysisException extends RuntimeException {

    private final ErrorCode errorCode;

    public AnalysisException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Getter
    @AllArgsConstructor
    public enum ErrorCode implements BaseErrorCode {
        ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS_0001", "분석 데이터를 찾을 수 없습니다."),
        UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "ANALYSIS_0002", "권한이 없는 접근입니다."),
        INVALID_ANALYSIS_TYPE(HttpStatus.BAD_REQUEST, "ANALYSIS_0003", "잘못된 분석 유형입니다."),
        INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "ANALYSIS_0004", "잘못된 날짜 범위입니다."),
        INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "ANALYSIS_0005", "잘못된 금액입니다."),
        INVALID_ACCOUNT(HttpStatus.BAD_REQUEST, "ANALYSIS_0006", "잘못된 계좌입니다."),
        INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "ANALYSIS_0007", "잘못된 카테고리입니다."),
        INVALID_RESULT(HttpStatus.BAD_REQUEST, "ANALYSIS_0008", "잘못된 결과입니다.");

        private final HttpStatus status;
        private final String code;
        private final String message;
    }
}
