package greencity.validator;

import greencity.annotations.EventValidation;
import greencity.dto.event.EventDayDto;
import greencity.dto.event.EventDetailsUpdate;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {

    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private ConstraintValidatorContext context;

    private EventDetailsUpdate validEvent;

    @BeforeEach
    void setUp() {
        validEvent = new EventDetailsUpdate();
        validEvent.setId(1L);
        validEvent.setIsOpenEvent(true);
        validEvent.setTitle("Valid event title");
        var eventDay1 = new EventDayDto();
        eventDay1.setId(1L);
        eventDay1.setEventDate(LocalDate.now().plusDays(1));
        eventDay1.setEventStartTime(LocalTime.now());
        eventDay1.setEventEndTime(LocalTime.now().plusHours(2));
        var eventDay2 = new EventDayDto();
        eventDay2.setId(2L);
        eventDay2.setEventDate(LocalDate.now().plusDays(2));
        eventDay2.setEventStartTime(LocalTime.now());
        eventDay2.setEventEndTime(LocalTime.now().plusHours(4));
        validEvent.setEventDays(List.of(eventDay1, eventDay2));
        eventValidator.initialize(new EventValidation() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return EventValidation.class;
            }

            @Override
            public String message() {
                return "Invalid data of event.";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int titleMaxSize() {
                return 70;
            }
        });

    }


    @Test
    public void validEventTest() {
        assertTrue(eventValidator.isValid(validEvent, context));
    }

    @Test
    public void eventTitleEmptyGetFalse() {
        validEvent.setTitle("");
        assertFalse(eventValidator.isValid(validEvent, context));
    }

    @Test
    public void eventTitleLengthBiggerThenMaxSizeGetFalse() {
        validEvent.setTitle("A".repeat(71));
        assertFalse(eventValidator.isValid(validEvent, context));
    }

    @Test
    public void eventDaysHaveEmptyDateFieldsGetFalse() {
        validEvent.getEventDays().get(0).setEventDate(null);
        assertFalse(eventValidator.isValid(validEvent, context));
    }

    @Test
    public void eventDaysHaveDuplicatedDatesGetFalse() {
        validEvent.getEventDays().forEach(eventDayDto -> eventDayDto.setEventDate(LocalDate.now()));
        assertFalse(eventValidator.isValid(validEvent, context));
    }

    @Test
    public void eventDayHaveEmptyTimeFieldGetFalse() {
        validEvent.getEventDays().get(0).setEventStartTime(null);
        assertFalse(eventValidator.isValid(validEvent, context));
    }

    @Test
    public void eventDayHasDateInPastGetFalse() {
        validEvent.getEventDays().get(1).setEventDate(LocalDate.EPOCH);
        assertFalse(eventValidator.isValid(validEvent, context));
    }
}
