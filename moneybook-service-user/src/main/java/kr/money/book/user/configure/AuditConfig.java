package kr.money.book.user.configure;

import kr.money.book.user.configure.auditing.AuditorAwareMapping;
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
