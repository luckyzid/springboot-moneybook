package kr.money.book.account.web.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.money.book.account.web.application.AccountService;
import kr.money.book.redis.constants.CommandKey;
import kr.money.book.redis.pubsub.RedisSubscriber;
import kr.money.book.redis.valueobject.CommandMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountRedisSubscribeService extends RedisSubscriber {

    private final AccountService accountService;

    public AccountRedisSubscribeService(AccountService accountService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.accountService = accountService;
    }

    @Override
    @Transactional
    protected void handleMessage(String channel, CommandMessage message) {

        String userKey = message.getUserKey();
        try {
            switch (channel) {
                case CommandKey.COMMAND_SYNC -> accountService.syncUserCache(userKey);
                case CommandKey.COMMAND_DELETE -> accountService.deleteAccount(userKey);
                default -> log.warn("Unknown channel/command received: {}", channel);
            }
        } catch (Exception e) {
            log.error("Error {} for userKey {} at {}: {}", channel, userKey, message.getTimestamp(),
                e.getMessage());
        }
    }
}
