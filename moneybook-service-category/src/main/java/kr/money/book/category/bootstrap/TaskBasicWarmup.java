package kr.money.book.category.bootstrap;

import kr.money.book.common.bootstrap.WarmupTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskBasicWarmup implements WarmupTask {

    @Override
    public void warmup() {
        log.info("TaskBasicWarmup warmup");
    }
}
