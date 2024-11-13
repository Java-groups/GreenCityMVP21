package greencity.validator;

import greencity.annotations.EventValidation;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventDayDto;
import greencity.dto.event.EventRequestDto;
import jakarta.validation.ConstraintValidatorContext;
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

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {

    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private ConstraintValidatorContext context;

    private EventRequestDto validEvent;

    @BeforeEach
    void setUp() {
        validEvent = new EventRequestDto();
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
        validEvent.setEventDays(List.of(eventDay1,eventDay2));
        eventValidator.initialize(new EventValidation() {
            @Override
            public Class<? extends Annotation> annotationType() { return EventValidation.class; }

            @Override
            public String message() {
                return "Invalid data of event.";
            }

            @Override
            public int titleMaxSize() { return 70; }
        });

    }


    @Test
    public void validEventTest() {
        assertTrue(eventValidator.isValid(validEvent,context));
    }

    @Test
    public void eventTitleEmptyGetException(){
        validEvent.setTitle("");
        var exception = assertThrows(IllegalArgumentException.class,()->eventValidator.isValid(validEvent,context));
        assertEquals(ErrorMessage.EMPTY_FIELD, exception.getMessage());
    }

    @Test
    public void eventTitleLengthBiggerThenMaxSizeGetException (){
        validEvent.setTitle("A".repeat(71));
        var exception = assertThrows(IllegalArgumentException.class,()->eventValidator.isValid(validEvent,context));
        assertEquals(ErrorMessage.REACHED_MAX_SIZE+70,exception.getMessage());
    }

    @Test
    public void eventDaysHaveEmptyDateFieldsGetException(){
        validEvent.getEventDays().get(0).setEventDate(null);
        var exception = assertThrows(IllegalArgumentException.class,()->eventValidator.isValid(validEvent,context));
        assertEquals(ErrorMessage.EMPTY_FIELD + "event days",exception.getMessage());
    }

    @Test
    public void eventDaysHaveDuplicatedDates(){
        validEvent.getEventDays().forEach(eventDayDto -> eventDayDto.setEventDate(LocalDate.now()));
        var exception = assertThrows(IllegalArgumentException.class,()->eventValidator.isValid(validEvent,context));
        assertEquals(ErrorMessage.DATE_EXISTS_IN_EVENTS_DATES,exception.getMessage());
    }

    @Test
    public void eventDayHaveEmptyTimeField (){
        validEvent.getEventDays().get(0).setEventStartTime(null);
        var exception = assertThrows(IllegalArgumentException.class,()->eventValidator.isValid(validEvent,context));
        assertEquals(ErrorMessage.EMPTY_FIELD + "start and end time", exception.getMessage());
    }

    @Test
    public void eventDayHasDateInPast (){
        validEvent.getEventDays().get(1).setEventDate(LocalDate.EPOCH);
        var exception = assertThrows(IllegalArgumentException.class,()->eventValidator.isValid(validEvent,context));
        assertEquals(ErrorMessage.EVENT_DAY_IN_PAST, exception.getMessage());
    }
}
