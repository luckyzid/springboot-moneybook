package kr.money.book.ehcache.configurations;

import java.time.Duration;
import javax.cache.Caching;
import kr.money.book.ehcache.configurations.properties.EhcacheProperties;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(EhcacheProperties.class)
public class EhcacheConfiguration {

    private final EhcacheProperties ehcacheProperties;

    public EhcacheConfiguration(EhcacheProperties ehcacheProperties) {
        this.ehcacheProperties = ehcacheProperties;
    }

    @Bean
    public CacheManager ehCacheManager() {

        if (!ehcacheProperties.isEnable()) {
            throw new IllegalStateException("Ehcache is disabled");
        }

        javax.cache.CacheManager cacheManager = Caching
            .getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider")
            .getCacheManager();

        if (ehcacheProperties.getCaches() != null) {
            for (EhcacheProperties.CacheConfig cacheConfig : ehcacheProperties.getCaches()) {
                String cacheName = cacheConfig.getName();
                if (cacheManager.getCache(cacheName, String.class, Object.class) == null) {
                    cacheManager.createCache(
                        cacheName,
                        Eh107Configuration.fromEhcacheCacheConfiguration(createCacheConfiguration(cacheConfig))
                    );
                }
            }
        }

        return new JCacheCacheManager(cacheManager);
    }

    private CacheConfiguration<String, Object> createCacheConfiguration(EhcacheProperties.CacheConfig cacheConfig) {

        return CacheConfigurationBuilder
            .newCacheConfigurationBuilder(String.class, Object.class,
                ResourcePoolsBuilder.heap(cacheConfig.getHeapSize()))
            .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(cacheConfig.getTtl() / 2)))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(cacheConfig.getTtl())))
            .build();
    }
}
