package kr.money.book.user.configure;

import kr.money.book.web.configurations.WebMvcConfiguration;
import kr.money.book.user.configure.interceptors.LocalContextHolderInterceptor;
import kr.money.book.user.configure.interceptors.UserRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class WebMvcConfig extends WebMvcConfiguration {

    private final UserRequestInterceptor userRequestInterceptor;
    private final LocalContextHolderInterceptor localContextHolderInterceptor;

    public WebMvcConfig(
        UserRequestInterceptor userRequestInterceptor,
        LocalContextHolderInterceptor localContextHolderInterceptor) {

        this.userRequestInterceptor = userRequestInterceptor;
        this.localContextHolderInterceptor = localContextHolderInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(localContextHolderInterceptor).addPathPatterns("/login/oauth2/**");
        registry.addInterceptor(userRequestInterceptor)
            .addPathPatterns("/user/**", "/login/oauth2/**")
            .excludePathPatterns("/user/register", "/user/login/**");
    }
}
