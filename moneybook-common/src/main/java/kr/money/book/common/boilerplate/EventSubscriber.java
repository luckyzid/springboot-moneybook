package kr.money.book.common.boilerplate;

public interface EventSubscriber<E> {
    void handle(E event);

    Class<E> getEventType();
}
