package kr.money.book.ehcache.configurations.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "config.ehcache")
public class EhcacheProperties {

    private boolean enable = true;
    private List<CacheConfig> caches;

    @Getter
    @Setter
    public static class CacheConfig {

        private String name;
        private long ttl = 21600;
        private int heapSize = 2000;
    }
}
