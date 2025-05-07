package kr.money.book.common.boilerplate.checker;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;

@Getter
public class Checkers<T, R> {

    private final PredicateChecker<T> extraValidator;
    private final FunctionChecker<T, R> extraInterceptor;

    public Checkers(PredicateChecker<T> extraValidator, FunctionChecker<T, R> extraInterceptor) {
        this.extraValidator = extraValidator;
        this.extraInterceptor = extraInterceptor;
    }

    public Optional<R> invokeInterceptor(final FunctionChecker<T, R> interceptor) {

        if (interceptor == null) {
            return Optional.empty();
        }

        final T param = interceptor.getParam();
        final Function<T, Optional<R>> function = interceptor.getFunction();

        return function.apply(param);
    }

    public boolean invokeValidator(final PredicateChecker<T> validator) {

        if (validator == null) {
            return true;
        }

        final T param = validator.getParam();
        final Predicate<T> predicate = validator.getPredicate();

        return predicate.test(param);
    }
}
