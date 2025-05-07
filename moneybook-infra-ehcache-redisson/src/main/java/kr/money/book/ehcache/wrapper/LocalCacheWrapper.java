package kr.money.book.ehcache.wrapper;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import kr.money.book.ehcache.valueobject.LocalCacheMap;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocalCacheWrapper {

    private final CacheManager cacheManager;
    private final RedissonClient redissonClient;

    public LocalCacheWrapper(CacheManager cacheManager, RedissonClient redissonClient) {
        this.cacheManager = cacheManager;
        this.redissonClient = redissonClient;
    }

    public void put(String cacheName, String key, Object value) {

        log.info("put: {} -> {}", cacheName, key);
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache put '{}' not found", cacheName);
        }

        cache.put(key, value);
        log.info("Put to cache '{}' with key: {}", cacheName, key);
    }

    public <T> T get(String cacheName, String key, Class<T> type) {

        log.info("get: {} -> {}", cacheName, key);
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache get '{}' not found", cacheName);
            return null;
        }

        Cache.ValueWrapper wrapper = cache.get(key);

        return wrapper != null ? type.cast(wrapper.get()) : null;
    }

    public void evict(String cacheName, String key) {

        log.info("evict: {} -> {}", cacheName, key);
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache evict '{}' not found", cacheName);
        }

        cache.evict(key);
        log.info("Evicted from cache '{}' with key: {}", cacheName, key);
    }

    public void updateWithLock(String cacheName, String key, Function<LocalCacheMap, LocalCacheMap> updater) {

        log.info("updateWithLock: {} -> {}", cacheName, key);
        RLock lock = redissonClient.getLock("lock:localCache:" + key);
        try {
            if (!lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                log.warn("Failed to acquire lock for cache '{}' with key: {}", cacheName, key);
                return;
            }
            try {
                LocalCacheMap current = get(cacheName, key, LocalCacheMap.class);
                LocalCacheMap updated = updater.apply(current);
                put(cacheName, key, updated);
                log.info("Lock update succeeded for cache '{}' with key: {}", cacheName, key);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while acquiring lock for cache '{}' with key: {}", cacheName, key, e);
        }
    }
}
