package kr.money.book.category.configure;

import kr.money.book.category.configure.auditing.AuditorAwareMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {

        return new AuditorAwareMapping();
    }
}
