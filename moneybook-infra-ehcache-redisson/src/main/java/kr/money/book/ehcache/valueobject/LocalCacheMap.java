package kr.money.book.ehcache.valueobject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCacheMap {

    private final Map<String, Object> localMap = new ConcurrentHashMap<>();

    public void set(String key, Object value) {

        localMap.put(key, value);
    }

    public Object get(String key) {

        return localMap.get(key);
    }

    public void del(String key) {

        localMap.remove(key);
    }
}

