package greencity.validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ClockWrapper clockWrapper(Clock clock) {
        return new ClockWrapper(clock);
    }
}
