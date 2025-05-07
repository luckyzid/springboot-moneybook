package kr.money.book.redisson.configurations;

import kr.money.book.redisson.configurations.properties.RedissonCacheProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedissonCacheProperties.class)
public class RedissonCacheConfiguration {

	private final RedissonCacheProperties redissonCacheProperties;

	public RedissonCacheConfiguration(RedissonCacheProperties redissonCacheProperties) {
		this.redissonCacheProperties = redissonCacheProperties;
	}

	@Bean
	public RedissonClient redissonClient() {

		if (!redissonCacheProperties.isEnable()) {
			throw new IllegalStateException("Redisson is disabled");
		}

		return redissonCacheProperties.isClusterMode()
				? createClusterRedissonClient()
				: createStandaloneRedissonClient();
	}

	private RedissonClient createClusterRedissonClient() {

		Config config = new Config();
		config.useClusterServers()
				.setScanInterval(2000)
				.setConnectTimeout(10000)
				.setTimeout(3000);

		for (RedissonCacheProperties.ClusterConfig cluster : redissonCacheProperties.getCluster()) {
			String redissonConfigUrl = String.format("redis://%s:%d", cluster.getHost(), cluster.getPort());
			config.useClusterServers().addNodeAddress(redissonConfigUrl);
		}

		return Redisson.create(config);
	}

	private RedissonClient createStandaloneRedissonClient() {

		String redissonConfigUrl = String.format("redis://%s:%d", redissonCacheProperties.getHost(), redissonCacheProperties.getPort());
		Config config = new Config();
		config.useSingleServer()
				.setAddress(redissonConfigUrl)
				.setConnectTimeout(10000)
				.setTimeout(3000);

		return Redisson.create(config);
	}
}
