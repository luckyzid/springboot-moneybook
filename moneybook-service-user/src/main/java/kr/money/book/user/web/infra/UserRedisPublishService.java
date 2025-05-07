package kr.money.book.user.web.infra;

import kr.money.book.redis.constants.CommandKey;
import kr.money.book.redis.pubsub.RedisPublisher;
import kr.money.book.redis.valueobject.CommandMessage;
import org.springframework.stereotype.Component;

@Component
public class UserRedisPublishService {

    private final RedisPublisher redisPublisher;

    public UserRedisPublishService(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

    public void sync(String userKey) {

        redisPublisher.publish(CommandKey.COMMAND_SYNC, CommandMessage.of(userKey));
    }

    public void delete(String userKey) {

        redisPublisher.publish(CommandKey.COMMAND_DELETE, CommandMessage.of(userKey));
    }
}
