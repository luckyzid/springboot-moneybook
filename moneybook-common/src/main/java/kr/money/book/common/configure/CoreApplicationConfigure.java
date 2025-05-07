package kr.money.book.common.configure;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CoreApplicationConfigure {

    private final Environment environment;

    public CoreApplicationConfigure(Environment environment) {
        this.environment = environment;
    }

    public String property(String name) {

        return Objects.requireNonNull(
            environment.getProperty(name), () -> "Property '" + name + "' does not exist"
        );
    }

    public String activeProfile() {

        return Arrays.stream(environment.getActiveProfiles())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No active profiles set"));
    }
}
