package kr.money.book.web.exceptions;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getStatus();

    String getCode();

    String getMessage();
}
