package kr.money.book.abtest.allocator;

import java.util.Objects;
import java.util.Optional;
import kr.money.book.abtest.context.AbTestUserContext;
import org.springframework.stereotype.Component;

/**
 * Provides a sticky allocation so that the same user always receives the same variant
 * for a given experiment by hashing the experiment key together with the user identifier.
 */
@Component
public class StickyHashAbTestVariantAllocator implements AbTestVariantAllocator {

    @Override
    public String allocate(String experimentKey, AbTestUserContext userContext) {

        Long userId = resolveUserId(userContext);
        if (userId == null) {
            return "control";
        }

        int hash = Math.abs(Objects.hash(experimentKey, userId));
        return (hash % 2 == 0) ? "control" : "test";
    }

    private Long resolveUserId(AbTestUserContext userContext) {

        Long userId = userContext.getUserId();
        if (userId != null) {
            return userId;
        }

        return Optional.ofNullable(userContext.attribute("userKey"))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(String::hashCode)
            .map(Integer::longValue)
            .orElse(null);
    }
}
