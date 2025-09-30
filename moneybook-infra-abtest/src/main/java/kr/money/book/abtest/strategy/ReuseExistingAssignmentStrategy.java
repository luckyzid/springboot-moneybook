package kr.money.book.abtest.strategy;

import java.util.Optional;
import kr.money.book.abtest.context.AbTestAssignment;
import kr.money.book.abtest.context.AbTestUserContext;
import kr.money.book.common.boilerplate.checker.FunctionChecker;
import org.springframework.stereotype.Component;

@Component
public class ReuseExistingAssignmentStrategy implements AbTestAssignmentReuseStrategy {

    @Override
    public Optional<Boolean> decide(AbTestUserContext userContext,
                                    Optional<AbTestAssignment> existingAssignment) {

        FunctionChecker<Optional<AbTestAssignment>, Boolean> checker =
            FunctionChecker.<Optional<AbTestAssignment>, Boolean>builder()
                .param(existingAssignment)
                .function(optional -> optional.map(event -> Boolean.TRUE))
                .build();

        return checker.getFunction().apply(checker.getParam());
    }
}
