package kr.money.book.abtest.allocator;

import kr.money.book.abtest.context.AbTestUserContext;

@FunctionalInterface
public interface AbTestVariantAllocator {

    String allocate(String experimentKey, AbTestUserContext userContext);
}
