package kr.money.book.auth.redis.service;

import java.util.concurrent.TimeUnit;
import kr.money.book.redis.wrapper.RedisWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenCacheService {

    private final RedisWrapper redisWrapper;
    private static final String REDIS_TOKEN_KEY = "refresh_key_%s";

    public TokenCacheService(RedisWrapper redisWrapper) {
        this.redisWrapper = redisWrapper;
    }

    public void set(String userKey, String token, long tokenValidity) {

        redisWrapper.set(getRedisKey(userKey), token, tokenValidity, TimeUnit.SECONDS);
    }

    public String get(String userKey) {

        return redisWrapper.get(getRedisKey(userKey));
    }

    public void del(String userKey) {

        redisWrapper.del(getRedisKey(userKey));
    }

    private String getRedisKey(String userKey) {

        return String.format(REDIS_TOKEN_KEY, userKey);
    }
}
