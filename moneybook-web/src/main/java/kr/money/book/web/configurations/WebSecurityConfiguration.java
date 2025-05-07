package kr.money.book.web.configurations;

import kr.money.book.web.configurations.wrapper.WebSecurityConfigWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(
    value = "moneybook.auto.configure.web.security.enable",
    havingValue = "true",
    matchIfMissing = true
)
public class WebSecurityConfiguration extends WebSecurityConfigWrapper {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureCommon(http);
        return http.build();
    }
}
