package kr.money.book.web.configurations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(
    value = "moneybook.auto.configure.web.mvc.enable",
    havingValue = "true",
    matchIfMissing = true
)
public class WebMvcConfiguration implements WebMvcConfigurer {

    private static final long MAX_AGE = 86_400L;
    private static final String[] ALLOW_ORIGIN_METHODS = {"*"};
    private static final String[] ALLOW_ORIGIN_HEADERS = {"*"};
    private static final String[] ALLOW_ORIGIN_DOMAINS = {
        "http://localhost",
        "http://localhost:8080",
        "http://localhost:8180",
        "http://localhost:8280",
        "http://localhost:8380",
        "http://localhost:8480",
        "http://localhost:8580"
    };

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/swagger-ui/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedOriginPatterns(ALLOW_ORIGIN_DOMAINS)
            .allowedMethods(ALLOW_ORIGIN_METHODS)
            .allowedHeaders(ALLOW_ORIGIN_HEADERS)
            .allowCredentials(true)
            .maxAge(MAX_AGE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("favicon.ico")
            .addResourceLocations("classpath:/static/favicon.ico");
    }
}
