package kr.money.book.redis.configurations;

import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import java.time.Duration;
import kr.money.book.redis.configurations.properties.RedisCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisCacheProperties.class)
public class RedisCacheConfiguration {

    private final RedisCacheProperties redisCacheProperties;

    public RedisCacheConfiguration(RedisCacheProperties redisCacheProperties) {
        this.redisCacheProperties = redisCacheProperties;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        if (!redisCacheProperties.isEnable()) {
            throw new IllegalStateException("Redis is disabled");
        }

        return redisCacheProperties.isClusterMode()
            ? createClusterConnectionFactory()
            : createStandaloneConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    private RedisConnectionFactory createClusterConnectionFactory() {

        RedisClusterConfiguration clusterConfig = configureClusterConfig();

        return new LettuceConnectionFactory(clusterConfig, getLettuceClientConfiguration());
    }

    private RedisClusterConfiguration configureClusterConfig() {

        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        for (RedisCacheProperties.ClusterConfig cluster : redisCacheProperties.getCluster()) {
            RedisNode clusterNode = new RedisNode(cluster.getHost(), cluster.getPort());
            clusterConfig.addClusterNode(clusterNode);
        }

        return clusterConfig;
    }

    private RedisConnectionFactory createStandaloneConnectionFactory() {

        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(
            redisCacheProperties.getHost(),
            redisCacheProperties.getPort()
        );

        return new LettuceConnectionFactory(standaloneConfig, getLettuceClientConfiguration());
    }

    private static LettuceClientConfiguration getLettuceClientConfiguration() {

        return LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(3))
            .shutdownTimeout(Duration.ofMillis(100))
            .clientOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                    .enablePeriodicRefresh(Duration.ofSeconds(10))
                    .enableAllAdaptiveRefreshTriggers()
                    .build())
                .timeoutOptions(TimeoutOptions.enabled())
                .build())
            .build();
    }
}
