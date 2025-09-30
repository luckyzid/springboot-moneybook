package kr.money.book.abtest.condition;

import kr.money.book.abtest.context.AbTestUserContext;
import org.springframework.stereotype.Component;

/**
 * Allows only premium users ("isPremium" attribute equals true) to join the experiment.
 */
@Component
public class PremiumUserAbTestCondition implements AbTestCondition {

    private static final String ATTRIBUTE_KEY = "isPremium";

    @Override
    public boolean matches(AbTestUserContext userContext) {

        Object attribute = userContext.attribute(ATTRIBUTE_KEY);
        return attribute instanceof Boolean bool && bool;
    }
}
