package kr.money.book.web.configurations.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
public class WebMvcConfigWrapper implements WebMvcConfigurer {

    protected static final long MAX_AGE = 86400L;
    protected static final String[] ALLOW_ORIGIN_METHODS = {"*"};
    protected static final String[] ALLOW_ORIGIN_HEADERS = {"*"};
    protected static final String[] DEFAULT_ALLOW_ORIGIN_DOMAINS = {};

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedOriginPatterns(getAllowOriginDomains())
            .allowedMethods(ALLOW_ORIGIN_METHODS)
            .allowedHeaders(ALLOW_ORIGIN_HEADERS)
            .allowCredentials(true)
            .maxAge(MAX_AGE);
    }

    protected String[] getAllowOriginDomains() {
        return DEFAULT_ALLOW_ORIGIN_DOMAINS;
    }
}
