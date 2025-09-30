package kr.money.book.abtest.allocator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import kr.money.book.abtest.context.AbTestUserContext;
import org.springframework.stereotype.Component;

@Component
public class DefaultAbTestVariantAllocator implements AbTestVariantAllocator {

    private final ConcurrentMap<String, AtomicInteger> experimentCounters = new ConcurrentHashMap<>();

    @Override
    public String allocate(String experimentKey, AbTestUserContext userContext) {

        AtomicInteger counter = experimentCounters.computeIfAbsent(experimentKey, key -> new AtomicInteger(0));
        int next = counter.getAndIncrement();

        return (next % 2 == 0) ? "control" : "test";
    }
}
