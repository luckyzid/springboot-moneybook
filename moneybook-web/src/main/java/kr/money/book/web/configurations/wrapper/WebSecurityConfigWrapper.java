package kr.money.book.web.configurations.wrapper;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
public class WebSecurityConfigWrapper {

    private static final String[] WEB_IGNORE_URLS = {
        "/",
        "/error",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/webjars/**",
        "/configuration/**",
        "/actuator/**",
        "/application/**",
        "/favicon.ico"
    };

    public RequestMatcher[] getWebIgnoreMatchers() {
        return List.of(WEB_IGNORE_URLS)
            .stream()
            .map(AntPathRequestMatcher::new)
            .toArray(RequestMatcher[]::new);
    }

    protected void configureCommon(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .logout(logout -> logout.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(WEB_IGNORE_URLS);
    }

    @Bean
    public RequestRejectedHandler requestRejectedHandler() {
        return (request, response, exception) -> {
            log.warn("{} for this URL: {}", exception.getMessage(), request.getRequestURL());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        };
    }
}
