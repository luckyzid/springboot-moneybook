package kr.money.book.budget.web.exceptions;

import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BudgetException extends RuntimeException {

    private final ErrorCode errorCode;

    public BudgetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Getter
    @AllArgsConstructor
    public enum ErrorCode implements BaseErrorCode {
        BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "BUDGET_0001", "예산 정보를 찾을 수 없습니다."),
        UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "BUDGET_0002", "권한이 없는 예산 정보 접근입니다."),
        INVALID_BUDGET_INPUT(HttpStatus.BAD_REQUEST, "BUDGET_0003", "예산 정보가 잘못되었습니다."),
        INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "BUDGET_0004", "잘못된 금액입니다.");

        private final HttpStatus status;
        private final String code;
        private final String message;
    }
}
