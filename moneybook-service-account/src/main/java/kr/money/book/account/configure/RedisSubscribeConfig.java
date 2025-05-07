package kr.money.book.account.configure;

import kr.money.book.account.web.infra.AccountRedisSubscribeService;
import kr.money.book.redis.constants.CommandKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscribeConfig {

    @Bean(name = "AccountRedisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory,
        AccountRedisSubscribeService accountRedisSubscribeService) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener(accountRedisSubscribeService,
            new ChannelTopic(CommandKey.COMMAND_SYNC));
        container.addMessageListener(accountRedisSubscribeService,
            new ChannelTopic(CommandKey.COMMAND_DELETE));

        return container;
    }
}
