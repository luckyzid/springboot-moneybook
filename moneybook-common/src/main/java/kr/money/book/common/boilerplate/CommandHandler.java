package kr.money.book.common.boilerplate;

public interface CommandHandler<C, R> {
    R handle(C command);
}
