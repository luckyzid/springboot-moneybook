package kr.money.book.abtest.condition;

import kr.money.book.abtest.context.AbTestUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Allows users into the experiment when their userId modulo N matches the configured remainder.
 * Useful for deterministic sampling without pre-defined segments.
 */
@Component
@RequiredArgsConstructor
public class ModNUserSegmentationCondition implements AbTestCondition {

    private final ModNUserSegmentationConditionProperties properties;

    @Override
    public boolean matches(AbTestUserContext userContext) {

        Long userId = userContext.getUserId();
        if (userId == null) {
            return false;
        }

        int modulus = properties.normalizedModulus();
        int targetRemainder = properties.normalizedTargetRemainder();

        return Math.floorMod(userId, modulus) == targetRemainder;
    }
}
