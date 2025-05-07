package kr.money.book.budget.web.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.money.book.budget.web.application.BudgetService;
import kr.money.book.redis.constants.CommandKey;
import kr.money.book.redis.pubsub.RedisSubscriber;
import kr.money.book.redis.valueobject.CommandMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BudgetRedisSubscribeService extends RedisSubscriber {

    private final BudgetService budgetService;

    public BudgetRedisSubscribeService(BudgetService budgetService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.budgetService = budgetService;
    }

    @Override
    @Transactional
    protected void handleMessage(String channel, CommandMessage message) {

        String userKey = message.getUserKey();

        try {
            switch (channel) {
                case CommandKey.COMMAND_DELETE -> budgetService.deleteAccount(userKey);
                default -> log.warn("Unknown channel/command received: {}", channel);
            }
        } catch (Exception e) {
            log.error("Error {} for userKey {} at {}: {}", channel, userKey, message.getTimestamp(), e.getMessage());
        }
    }
}
