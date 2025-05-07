package kr.money.book.analyze.configure;

import kr.money.book.analyze.configure.auditing.AuditorAwareMapping;
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
