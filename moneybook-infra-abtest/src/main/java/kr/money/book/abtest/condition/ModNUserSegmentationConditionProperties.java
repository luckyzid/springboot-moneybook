package kr.money.book.abtest.condition;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "moneybook.abtest.condition.mod-n")
@Getter
@Setter
public class ModNUserSegmentationConditionProperties {

    /**
     * Value for the modulus operation. Must be greater than zero.
     */
    private int modulus = 3;

    /**
     * Remainder that determines eligibility for the experiment.
     */
    private int targetRemainder = 1;

    int normalizedModulus() {

        return Math.max(1, modulus);
    }

    int normalizedTargetRemainder() {

        int mod = normalizedModulus();
        int remainder = targetRemainder % mod;
        return remainder < 0 ? remainder + mod : remainder;
    }
}
