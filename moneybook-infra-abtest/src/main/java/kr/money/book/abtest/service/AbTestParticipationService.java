package kr.money.book.abtest.service;

import java.util.Optional;
import kr.money.book.abtest.allocator.AbTestVariantAllocator;
import kr.money.book.abtest.condition.AbTestCondition;
import kr.money.book.abtest.context.AbTestAssignment;
import kr.money.book.abtest.context.AbTestUserContext;

public interface AbTestParticipationService {

    void recordParticipation(AbTestAssignment assignment,
                             Class<? extends AbTestCondition> conditionType,
                             Class<? extends AbTestVariantAllocator> allocatorType);

    Optional<AbTestAssignment> findExistingAssignment(String experimentKey, AbTestUserContext userContext);
}
