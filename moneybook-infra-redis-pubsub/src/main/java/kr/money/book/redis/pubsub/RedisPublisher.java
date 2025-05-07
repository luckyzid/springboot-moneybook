package kr.money.book.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.money.book.redis.valueobject.CommandMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(String channel, CommandMessage message) {

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(channel, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to serialize message: {}", message, e);
            throw new IllegalArgumentException("Failed to serialize message", e);
        }
    }
}
