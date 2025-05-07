package kr.money.book.helper;

import java.util.Optional;
import kr.money.book.ehcache.configurations.EhcacheConfiguration;
import kr.money.book.shorturl.configure.WebSecurityConfig;
import kr.money.book.shorturl.web.domain.mapper.ShortUrlInfoToShortUrlMapper;
import kr.money.book.shorturl.web.domain.mapper.ShortUrlToShortUrlInfoMapper;
import kr.money.book.shorturl.web.domain.repository.ShortUrlRepository;
import kr.money.book.shorturl.web.infra.ShortUrlPersistenceAdapter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@TestConfiguration
@ComponentScan(
    basePackages = {
        "kr.money.book.*.web.domain.repository",
        "kr.money.book.*.web.domain.entity",
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {WebSecurityConfig.class}
    )
)
@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@EnableCaching
@Import(EhcacheConfiguration.class)
public class DataSourceConfig {

    private static final String DEFAULT_USER_KEY = "test-user";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                return Optional.of(auth.getPrincipal().toString());
            }
            return Optional.of(DEFAULT_USER_KEY);
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/**");
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }

    @Bean
    public ShortUrlInfoToShortUrlMapper shortUrlInfoToShortUrlMapper() {
        return new ShortUrlInfoToShortUrlMapper();
    }

    @Bean
    public ShortUrlToShortUrlInfoMapper shortUrlToShortUrlInfoMapper() {
        return new ShortUrlToShortUrlInfoMapper();
    }

    @Bean
    public ShortUrlPersistenceAdapter shortUrlPersistenceAdapter(
        ShortUrlRepository shortUrlRepository,
        ShortUrlInfoToShortUrlMapper shortUrlInfoToShortUrlMapper,
        ShortUrlToShortUrlInfoMapper shortUrlToShortUrlInfoMapper) {

        return new ShortUrlPersistenceAdapter(
            shortUrlRepository,
            shortUrlInfoToShortUrlMapper,
            shortUrlToShortUrlInfoMapper
        );
    }
}
