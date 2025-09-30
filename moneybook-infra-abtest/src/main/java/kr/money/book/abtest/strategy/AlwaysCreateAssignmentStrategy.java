package kr.money.book.abtest.strategy;

import java.util.Optional;
import kr.money.book.abtest.context.AbTestAssignment;
import kr.money.book.abtest.context.AbTestUserContext;
import kr.money.book.common.boilerplate.checker.PredicateChecker;
import org.springframework.stereotype.Component;

@Component
public class AlwaysCreateAssignmentStrategy implements AbTestAssignmentReuseStrategy {

    @Override
    public Optional<Boolean> decide(AbTestUserContext userContext,
                                    Optional<AbTestAssignment> existingAssignment) {

        PredicateChecker<AbTestUserContext> checker = PredicateChecker.<AbTestUserContext>builder()
            .param(userContext)
            .predicate(context -> false)
            .build();

        return Optional.of(checker.getPredicate().test(checker.getParam()));
    }
}
