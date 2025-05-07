package kr.money.book.helper;

import java.util.Optional;
import kr.money.book.budget.configure.WebSecurityConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@TestConfiguration
@ComponentScan(
    basePackages = {
        "kr.money.book.*.web.domain.repository",
        "kr.money.book.*.web.domain.entity",
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {WebSecurityConfig.class}
    )
)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DataSourceConfig {

    private static final String DEFAULT_USER_KEY = "test-user";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                return Optional.of(auth.getPrincipal().toString());
            }
            return Optional.of(DEFAULT_USER_KEY);
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/**");
    }
}
