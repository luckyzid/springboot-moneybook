package kr.money.book.user.configure;

import kr.money.book.auth.filter.TokenExceptionFilter;
import kr.money.book.auth.redis.filter.TokenAuthenticationRedisFilter;
import kr.money.book.auth.redis.service.AuthenticationRedisService;
import kr.money.book.auth.security.AccessDeniedCustomHandler;
import kr.money.book.auth.security.AuthenticationCustomEntryPoint;
import kr.money.book.common.constants.Role;
import kr.money.book.user.configure.oauth.OAuth2AuthenticationSuccessHandler;
import kr.money.book.user.web.application.oauth.OAuth2CustomUserService;
import kr.money.book.web.configurations.wrapper.WebSecurityConfigWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigWrapper {

    private final OAuth2CustomUserService oAuth2CustomUserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final AuthenticationRedisService authenticationRedisService;
    private final String oauthLoginEndPoint;

    public WebSecurityConfig(
        OAuth2CustomUserService oAuth2CustomUserService,
        OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
        AuthenticationRedisService authenticationRedisService,
        @Value("${spring.security.oauth2.client.login.url:/login/oauth2/authorization}") String oauthLoginEndPoint) {

        this.oAuth2CustomUserService = oAuth2CustomUserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.authenticationRedisService = authenticationRedisService;
        this.oauthLoginEndPoint = oauthLoginEndPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        configureCommon(http);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(getWebIgnoreMatchers()).permitAll()
                .requestMatchers(additionalWhitelist()).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/user/manager/**")).hasRole(Role.MANAGE.name())
                .requestMatchers("/user/**").authenticated()
                .anyRequest().permitAll())
            .oauth2Login(oauth -> oauth
                .authorizationEndpoint(auth -> auth.baseUri(oauthLoginEndPoint))
                .userInfoEndpoint(user -> user.userService(oAuth2CustomUserService))
                .successHandler(oAuth2AuthenticationSuccessHandler))
            .addFilterBefore(new TokenAuthenticationRedisFilter(authenticationRedisService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new TokenExceptionFilter(), TokenAuthenticationRedisFilter.class)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new AuthenticationCustomEntryPoint())
                .accessDeniedHandler(new AccessDeniedCustomHandler()));

        return http.build();
    }

    private RequestMatcher[] additionalWhitelist() {

        return new RequestMatcher[]{
                new AntPathRequestMatcher("/login/oauth2/**"),
                new AntPathRequestMatcher("/user/login/**"),
                new AntPathRequestMatcher("/user/register"),
                new AntPathRequestMatcher("/user/token/refresh"),
        };
    }
}
