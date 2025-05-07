package kr.money.book.redis.wrapper;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisWrapper {

    private final RedisTemplate<String, String> redisTemplate;
    private final ListOperations<String, String> listOps;
    private final ValueOperations<String, String> valueOps;

    public RedisWrapper(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOps = redisTemplate.opsForList();
        this.valueOps = redisTemplate.opsForValue();
    }

    public void pushToList(String key, String value) {

        listOps.leftPush(key, value);
    }

    public String getValueFromList(String key, long index) {

        return listOps.index(key, index);
    }

    public List<String> getAllValuesFromList(String key) {

        return listOps.range(key, 0, -1);
    }

    public void removeFromList(String key, long count, String value) {

        listOps.remove(key, count, value);
    }

    public boolean expireKey(String key, long timeout, TimeUnit unit) {

        return redisTemplate.expire(key, timeout, unit);
    }

    public void set(String key, String value) {

        valueOps.set(key, value);
    }

    public void set(String key, String value, long timeout, TimeUnit ttlUnit) {

        valueOps.set(key, value, timeout, ttlUnit);
    }

    public String get(String key) {

        return valueOps.get(key);
    }

    public void del(String key) {

        redisTemplate.delete(key);
    }
}
