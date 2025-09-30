package kr.money.book.abtest.strategy;

import java.util.Optional;
import kr.money.book.abtest.context.AbTestAssignment;
import kr.money.book.abtest.context.AbTestUserContext;

@FunctionalInterface
public interface AbTestAssignmentReuseStrategy {

    Optional<Boolean> decide(AbTestUserContext userContext,
                             Optional<AbTestAssignment> existingAssignment);
}
