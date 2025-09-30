package kr.money.book.abtest.condition;

import kr.money.book.abtest.context.AbTestUserContext;

@FunctionalInterface
public interface AbTestCondition {

    boolean matches(AbTestUserContext userContext);
}
