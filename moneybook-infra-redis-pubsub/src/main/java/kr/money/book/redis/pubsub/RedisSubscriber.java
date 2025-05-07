package kr.money.book.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.money.book.redis.valueobject.CommandMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Slf4j
public abstract class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    public RedisSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("Received message from channel {}: {}", channel, body);

        try {
            CommandMessage data = objectMapper.readValue(body, CommandMessage.class);
            handleMessage(channel, data);
        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", body, e);
        }
    }

    protected abstract void handleMessage(String channel, CommandMessage data);
}
