package kr.money.book.budget.configure;

import kr.money.book.web.configurations.wrapper.WebSecurityConfigWrapper;
import kr.money.book.auth.redis.filter.TokenAuthenticationRedisFilter;
import kr.money.book.auth.filter.TokenExceptionFilter;
import kr.money.book.auth.security.AccessDeniedCustomHandler;
import kr.money.book.auth.security.AuthenticationCustomEntryPoint;
import kr.money.book.auth.redis.service.AuthenticationRedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigWrapper {

    private final AuthenticationRedisService authenticationRedisService;

    public WebSecurityConfig(AuthenticationRedisService authenticationRedisService) {
        this.authenticationRedisService = authenticationRedisService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureCommon(http);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(getWebIgnoreMatchers()).permitAll()
                .requestMatchers("/budget/**").authenticated()
                .anyRequest().permitAll())
            .addFilterBefore(new TokenAuthenticationRedisFilter(authenticationRedisService),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new TokenExceptionFilter(), TokenAuthenticationRedisFilter.class)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new AuthenticationCustomEntryPoint())
                .accessDeniedHandler(new AccessDeniedCustomHandler()));

        return http.build();
    }
}
