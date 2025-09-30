package kr.money.book.abtest.allocator;

import java.util.concurrent.ThreadLocalRandom;
import kr.money.book.abtest.context.AbTestUserContext;
import org.springframework.stereotype.Component;

/**
 * Allocates variants randomly with a 50:50 distribution between control and test.
 * This is helpful when the caller does not require stickiness between requests.
 */
@Component
public class RandomAbTestVariantAllocator implements AbTestVariantAllocator {

    @Override
    public String allocate(String experimentKey, AbTestUserContext userContext) {

        return ThreadLocalRandom.current().nextBoolean() ? "control" : "test";
    }
}
