package kr.money.book.user.bootstrap;

import java.util.Collections;
import kr.money.book.user.web.application.UserService;
import kr.money.book.user.web.domain.valueobject.UserInfo;
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

    private final UserService userService;

    public TaskSimulationWarmupService(UserService userService) {
        this.userService = userService;
    }

    @Async
    @Transactional
    public void warmupStartUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken("system", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            log.info("[WarmupSimulation] Starting warmup simulation");
            for (int i = 0; i < 10; i++) {
                String email = "warmup@dummy.com";
                userService.createWithEmail(
                    UserInfo.builder()
                        .email(i + email)
                        .password(email)
                        .name(email)
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
