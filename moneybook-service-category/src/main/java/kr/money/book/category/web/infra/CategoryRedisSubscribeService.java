package kr.money.book.category.web.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.money.book.category.web.application.CategoryService;
import kr.money.book.redis.constants.CommandKey;
import kr.money.book.redis.pubsub.RedisSubscriber;
import kr.money.book.redis.valueobject.CommandMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CategoryRedisSubscribeService extends RedisSubscriber {

    private final CategoryService categoryService;

    public CategoryRedisSubscribeService(CategoryService categoryService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.categoryService = categoryService;
    }

    @Override
    @Transactional
    protected void handleMessage(String channel, CommandMessage message) {

        String userKey = message.getUserKey();
        try {
            switch (channel) {
                case CommandKey.COMMAND_SYNC -> categoryService.syncUserCache(userKey);
                case CommandKey.COMMAND_DELETE -> categoryService.deleteCategory(userKey);
                default -> log.warn("Unknown channel/command received: {}", channel);
            }
        } catch (Exception e) {
            log.error("Error {} for userKey {} at {}: {}", channel, userKey, message.getTimestamp(), e.getMessage());
        }
    }
}
