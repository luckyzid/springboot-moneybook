package kr.money.book.category.configure;

import kr.money.book.category.web.infra.CategoryRedisSubscribeService;
import kr.money.book.redis.constants.CommandKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscribeConfig {

    @Bean(name = "CategoryRedisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory,
        CategoryRedisSubscribeService categoryRedisSubscribeService) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener(categoryRedisSubscribeService,
            new ChannelTopic(CommandKey.COMMAND_SYNC));
        container.addMessageListener(categoryRedisSubscribeService,
            new ChannelTopic(CommandKey.COMMAND_DELETE));

        return container;
    }
}
