package kr.money.book.common.boilerplate.checker;

import java.util.function.Predicate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PredicateChecker<T> {

    private final T param;
    private final Predicate<T> predicate;

    @Builder
    public PredicateChecker(T param, Predicate<T> predicate) {
        this.param = param;
        this.predicate = predicate;
    }
}
