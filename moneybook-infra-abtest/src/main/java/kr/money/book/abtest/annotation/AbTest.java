package kr.money.book.abtest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.money.book.abtest.allocator.AbTestVariantAllocator;
import kr.money.book.abtest.allocator.DefaultAbTestVariantAllocator;
import kr.money.book.abtest.condition.AbTestCondition;
import kr.money.book.abtest.condition.AlwaysTrueAbTestCondition;
import kr.money.book.abtest.strategy.AbTestAssignmentReuseStrategy;
import kr.money.book.abtest.strategy.AlwaysCreateAssignmentStrategy;

/**
 * Marks a method whose invocation should participate in an AB test experiment.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AbTest {

    /**
     * Unique identifier for the experiment (e.g. `billing.onboarding.v1`).
     */
    String experimentKey();

    /**
     * Condition that determines whether the current user can join the experiment.
     */
    Class<? extends AbTestCondition> condition() default AlwaysTrueAbTestCondition.class;

    /**
     * Allocator that decides which variant the user should receive. Several strategies are provided:
     * <ul>
     *     <li>{@code DefaultAbTestVariantAllocator} – alternates control/test on each invocation.</li>
     *     <li>{@code StickyHashAbTestVariantAllocator} – keeps the same variant for a user across calls.</li>
     *     <li>{@code RandomAbTestVariantAllocator} – splits traffic 50:50 randomly.</li>
     *     <li>{@code WeightedAbTestVariantAllocator} – uses configured percentages per experiment.</li>
     * </ul>
     */
    Class<? extends AbTestVariantAllocator> allocator() default DefaultAbTestVariantAllocator.class;

    /**
     * Strategy that decides whether an existing assignment should be reused or a new one should be created.
     */
    Class<? extends AbTestAssignmentReuseStrategy> reuseStrategy()
        default AlwaysCreateAssignmentStrategy.class;
}
