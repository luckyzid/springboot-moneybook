package kr.money.book.shorturl.configure;

import kr.money.book.auth.filter.TokenAuthenticationFilter;
import kr.money.book.auth.filter.TokenExceptionFilter;
import kr.money.book.auth.security.AccessDeniedCustomHandler;
import kr.money.book.auth.security.AuthenticationCustomEntryPoint;
import kr.money.book.auth.service.AuthenticationClaimsService;
import kr.money.book.common.constants.Role;
import kr.money.book.web.configurations.wrapper.WebSecurityConfigWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigWrapper {

    private final AuthenticationClaimsService authenticationClaimsService;

    public WebSecurityConfig(AuthenticationClaimsService authenticationClaimsService) {
        this.authenticationClaimsService = authenticationClaimsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureCommon(http);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(getWebIgnoreMatchers()).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/{shortKey}")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/shorturl/manager/**")).hasRole(Role.MANAGE.name())
                .anyRequest().permitAll())
            .addFilterBefore(new TokenAuthenticationFilter(authenticationClaimsService),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new TokenExceptionFilter(), TokenAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new AuthenticationCustomEntryPoint())
                .accessDeniedHandler(new AccessDeniedCustomHandler()));

        return http.build();
    }
}
