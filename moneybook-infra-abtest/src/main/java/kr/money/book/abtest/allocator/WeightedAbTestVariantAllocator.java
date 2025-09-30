package kr.money.book.abtest.allocator;

import java.util.concurrent.ThreadLocalRandom;
import kr.money.book.abtest.context.AbTestUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Splits traffic using the configured control percentage per experiment.
 * Example configuration:
 * moneybook.abtest.weighted.control-percentage.account.list.strategy=60
 */
@Component
@RequiredArgsConstructor
public class WeightedAbTestVariantAllocator implements AbTestVariantAllocator {

    private final WeightedAbTestVariantAllocatorProperties properties;

    @Override
    public String allocate(String experimentKey, AbTestUserContext userContext) {

        int controlPercentage = normalize(properties.controlPercentage(experimentKey));
        int randomValue = ThreadLocalRandom.current().nextInt(100);

        return randomValue < controlPercentage ? "control" : "test";
    }

    private int normalize(int percentage) {

        if (percentage < 0) {
            return 0;
        }

        if (percentage > 100) {
            return 100;
        }

        return percentage;
    }
}
