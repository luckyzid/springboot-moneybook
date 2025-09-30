package kr.money.book.abtest.allocator;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "moneybook.abtest.weighted")
@Getter
@Setter
public class WeightedAbTestVariantAllocatorProperties {

    /**
     * Map of experiment key to control variant percentage (0-100).
     * The remainder is allocated to the test variant.
     */
    private Map<String, Integer> controlPercentage = new HashMap<>();

    public int controlPercentage(String experimentKey) {

        return controlPercentage.getOrDefault(experimentKey, 50);
    }
}
