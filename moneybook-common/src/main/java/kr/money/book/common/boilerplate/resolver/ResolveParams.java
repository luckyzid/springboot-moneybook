package kr.money.book.common.boilerplate.resolver;

public interface ResolveParams<T, R> {

    T resolve(R param);
}
