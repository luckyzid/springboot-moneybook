package kr.money.book.budget.configure;

import kr.money.book.budget.web.infra.BudgetRedisSubscribeService;
import kr.money.book.redis.constants.CommandKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscribeConfig {

    @Bean(name = "BudgetRedisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory,
        BudgetRedisSubscribeService budgetRedisSubscribeService) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener(budgetRedisSubscribeService,
            new ChannelTopic(CommandKey.COMMAND_DELETE));

        return container;
    }
}
