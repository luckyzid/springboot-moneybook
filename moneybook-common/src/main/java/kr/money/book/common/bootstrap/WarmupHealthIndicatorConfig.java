package kr.money.book.common.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WarmupHealthIndicatorConfig {

    private final WarmupChecker warmupChecker;

    public WarmupHealthIndicatorConfig(WarmupChecker warmupChecker) {
        this.warmupChecker = warmupChecker;
    }

    @Bean
    public WarmupHealthIndicator warmupHealthIndicator(ApplicationContext context) {
        return new WarmupHealthIndicator((ConfigurableApplicationContext) context, warmupChecker);
    }
}
