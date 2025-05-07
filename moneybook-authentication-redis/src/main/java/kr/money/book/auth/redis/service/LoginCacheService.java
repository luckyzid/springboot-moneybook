package kr.money.book.auth.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.money.book.redis.wrapper.RedisWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginCacheService {

    private final RedisWrapper redisWrapper;
    private final ObjectMapper objectMapper;
    private static final String REDIS_LOGIN_CACHE_KEY = "login_cache_key_%s";

    public LoginCacheService(RedisWrapper redisWrapper, ObjectMapper objectMapper) {
        this.redisWrapper = redisWrapper;
        this.objectMapper = objectMapper;
    }

    public void set(String key, Object value) {

        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisWrapper.set(getRedisKey(key), jsonValue);
        } catch (Exception e) {
            throw new RuntimeException("Cache serialization error", e);
        }
    }

    public <T> T get(String key, Class<T> type) {

        String jsonValue = redisWrapper.get(getRedisKey(key));
        if (jsonValue == null) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonValue, type);
        } catch (Exception e) {
            return null;
        }
    }

    public void del(String key) {

        redisWrapper.del(getRedisKey(key));
    }

    private String getRedisKey(String userKey) {

        return String.format(REDIS_LOGIN_CACHE_KEY, userKey);
    }
}
