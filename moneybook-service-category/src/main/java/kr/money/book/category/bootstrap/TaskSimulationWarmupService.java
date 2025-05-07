package kr.money.book.category.bootstrap;

import java.util.Collections;
import kr.money.book.category.web.application.CategoryService;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
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

    private final CategoryService categoryService;

    public TaskSimulationWarmupService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Async
    @Transactional
    public void warmupStartUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken("system", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            log.info("[WarmupSimulation] Starting warmup simulation");
            for (int i = 0; i < 10; i++) {
                categoryService.createCategory(
                    CategoryInfo.builder()
                        .userKey("test-user" + i)
                        .name("test-category" + i)
                        .parentIdx(null)
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
