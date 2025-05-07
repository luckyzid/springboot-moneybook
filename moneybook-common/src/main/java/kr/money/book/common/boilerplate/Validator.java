package kr.money.book.common.boilerplate;

import jakarta.validation.ValidationException;

public interface Validator<T> {
    void validate(T target) throws ValidationException;
}
