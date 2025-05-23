package kr.money.book.user.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("Boot on after Bootstrap is running.");
    }
}
