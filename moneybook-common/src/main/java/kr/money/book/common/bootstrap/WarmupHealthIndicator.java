package kr.money.book.common.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class WarmupHealthIndicator extends AbstractHealthIndicator {

    private final WarmupChecker warmupChecker;

    public WarmupHealthIndicator(ConfigurableApplicationContext context,
        WarmupChecker warmupChecker) {
        this.warmupChecker = warmupChecker;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        log.info("Received /actuator/health request");
        if (warmupChecker.isWarmupCompleted()) {
            builder.up();
        } else {
            builder.outOfService().withDetail("warmup", "not completed");
        }
    }
}
