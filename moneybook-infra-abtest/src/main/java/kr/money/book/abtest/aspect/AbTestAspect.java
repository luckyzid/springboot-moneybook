package kr.money.book.abtest.aspect;

import java.util.Optional;
import kr.money.book.abtest.allocator.AbTestVariantAllocator;
import kr.money.book.abtest.annotation.AbTest;
import kr.money.book.abtest.condition.AbTestCondition;
import kr.money.book.abtest.context.AbTestAssignment;
import kr.money.book.abtest.context.AbTestContextHolder;
import kr.money.book.abtest.context.AbTestUserContext;
import kr.money.book.abtest.context.AbTestUserContextResolver;
import kr.money.book.abtest.service.AbTestParticipationService;
import kr.money.book.abtest.strategy.AbTestAssignmentReuseStrategy;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AbTestAspect {

    private final ApplicationContext applicationContext;
    private final AbTestParticipationService participationService;

    @Around("@annotation(abTest)")
    public Object evaluateExperiment(ProceedingJoinPoint joinPoint, AbTest abTest) throws Throwable {

        AbTestUserContext userContext = AbTestUserContextResolver.resolve(joinPoint.getArgs());
        AbTestCondition condition = applicationContext.getBean(abTest.condition());

        if (!condition.matches(userContext)) {
            return joinPoint.proceed();
        }

        Optional<AbTestAssignment> existingAssignment = participationService.findExistingAssignment(
            abTest.experimentKey(), userContext);

        AbTestAssignmentReuseStrategy reuseStrategy = applicationContext.getBean(abTest.reuseStrategy());
        Optional<Boolean> reuseDecision = reuseStrategy.decide(userContext, existingAssignment);

        AbTestAssignment assignment = null;
        boolean shouldPersist = true;
        AbTestVariantAllocator allocator = null;

        if (reuseDecision.orElse(false) && existingAssignment.isPresent()) {
            assignment = existingAssignment.get();
            shouldPersist = false;
        }

        if (assignment == null) {
            allocator = applicationContext.getBean(abTest.allocator());
            String variant = resolveVariant(abTest.experimentKey(), userContext, allocator);

            assignment = AbTestAssignment.builder()
                .experimentKey(abTest.experimentKey())
                .variant(variant)
                .userContext(userContext)
                .build();
        }

        AbTestContextHolder.setAssignment(assignment);

        try {
            Object result = joinPoint.proceed();
            if (shouldPersist) {
                participationService.recordParticipation(assignment, abTest.condition(), abTest.allocator());
            }
            return result;
        } finally {
            AbTestContextHolder.clear();
        }
    }

    private String resolveVariant(String experimentKey, AbTestUserContext context, AbTestVariantAllocator allocator) {

        String variant = allocator.allocate(experimentKey, context);
        return variant == null || variant.isBlank() ? "control" : variant;
    }
}
