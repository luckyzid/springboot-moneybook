package kr.money.book.common.boilerplate;

public interface DomainMapper<S, T> {
    T map(S source);
}
