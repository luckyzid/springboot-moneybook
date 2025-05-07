package kr.money.book.common.boilerplate;

public interface TypeHandler<T, R, P> {

    T type();

    R handle(P param);
}
