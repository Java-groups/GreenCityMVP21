package greencity.validator;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.Clock;

@Component
public class ClockWrapper {

    private final Clock clock;

    public ClockWrapper(Clock clock) {
        this.clock = clock;
    }

    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }
}
