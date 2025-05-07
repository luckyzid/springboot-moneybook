package kr.money.book.common.boilerplate.checker;

import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FunctionChecker<T, R> {

    private final T param;
    private final Function<T, Optional<R>> function;

    @Builder
    public FunctionChecker(T param, Function<T, Optional<R>> function) {
        this.param = param;
        this.function = function;
    }
}

