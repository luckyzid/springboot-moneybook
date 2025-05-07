package kr.money.book.user.bootstrap;

import kr.money.book.common.bootstrap.WarmupTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskSimulationWarmup implements WarmupTask {

    private final TaskSimulationWarmupService taskSimulationWarmupService;

    public TaskSimulationWarmup(TaskSimulationWarmupService taskSimulationWarmupService) {
        this.taskSimulationWarmupService = taskSimulationWarmupService;
    }

    @Override
    public void warmup() {
        log.info("TaskSimulationWarmup warmup");
        taskSimulationWarmupService.warmupStartUp();
    }
}
