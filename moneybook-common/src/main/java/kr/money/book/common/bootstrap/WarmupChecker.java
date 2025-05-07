package kr.money.book.common.bootstrap;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarmupChecker implements ApplicationListener<ApplicationReadyEvent> {

    private final List<WarmupTask> warmupTasks;

    private final Object lock = new Object();
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public WarmupChecker(List<WarmupTask> warmupTasks) {
        this.warmupTasks = warmupTasks;
    }

    public boolean isWarmupCompleted() {
        synchronized (lock) {
            if (!completed.get()) {
                executeWarmup();
            }
            return completed.get();
        }
    }

    private void executeWarmup() {
        log.info("Starting warmup tasks");
        List<WarmupTask> tasks = warmupTasks != null ? warmupTasks : Collections.emptyList();
        tasks.forEach(task -> {
            try {
                log.info("{} - warmup start", task.getClass().getSimpleName());
                task.warmup();
                log.info("{} - warmup end", task.getClass().getSimpleName());
            } catch (Exception ex) {
                log.warn("Warmup task failed: {}", ex.getMessage());
            }
        });
        completed.set(true);
        log.info("All warmup tasks completed");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        isWarmupCompleted();
    }
}
