package kr.money.book.abtest.condition;

import kr.money.book.abtest.context.AbTestUserContext;
import org.springframework.stereotype.Component;

@Component
public class AlwaysTrueAbTestCondition implements AbTestCondition {

    @Override
    public boolean matches(AbTestUserContext userContext) {
        return true;
    }
}
