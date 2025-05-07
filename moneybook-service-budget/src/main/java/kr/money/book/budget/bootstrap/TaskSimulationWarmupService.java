package kr.money.book.budget.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import kr.money.book.budget.web.application.BudgetService;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Component
public class TaskSimulationWarmupService {

    private final BudgetService budgetService;

    public TaskSimulationWarmupService(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Async
    @Transactional
    public void warmupStartUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken("system", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            log.info("[WarmupSimulation] Starting warmup simulation");
            for (int i = 0; i < 10; i++) {
                budgetService.createBudget(
                    BudgetInfo.builder()
                        .userKey("test-user" + i)
                        .type(BudgetType.EXPENSE)
                        .amount(BigDecimal.valueOf(i))
                        .comment("comment " + i)
                        .transactionDate(LocalDateTime.now())
                        .accountIdx(1l)
                        .categoryIdx(1l)
                        .build()
                );
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("[WarmupSimulation] Completed simulation (transaction rolled back)");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
