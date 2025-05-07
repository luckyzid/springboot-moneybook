package kr.money.book.account.bootstrap;

import java.util.Collections;
import kr.money.book.account.web.application.AccountService;
import kr.money.book.common.constants.AccountType;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Component
@Profile("!test")
public class TaskSimulationWarmupService {

    private final AccountService accountService;

    public TaskSimulationWarmupService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Async
    @Transactional
    public void warmupStartUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken("system", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            log.info("[WarmupSimulation] Starting warmup simulation");
            for (int i = 0; i < 10; i++) {
                accountService.createAccount(
                    AccountInfo.builder()
                        .userKey("test-user" + i)
                        .name("test-account" + i)
                        .type(AccountType.BANK)
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
