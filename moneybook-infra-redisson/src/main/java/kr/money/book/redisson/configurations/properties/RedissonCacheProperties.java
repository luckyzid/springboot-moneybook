package kr.money.book.redisson.configurations.properties;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.redisson")
public class RedissonCacheProperties {
	private boolean enable = false;
	private boolean clusterMode = false;
	private String host = "127.0.0.1";
	private Integer port = 6379;
	private List<ClusterConfig> cluster;

	@Getter
	@Setter
	public static class ClusterConfig {
		private String host = "127.0.0.1";
		private Integer port = 6379;
	}
}
