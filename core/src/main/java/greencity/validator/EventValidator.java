package greencity.validator;

import greencity.annotations.EventValidation;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventDayDto;
import greencity.dto.event.EventRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class EventValidator implements ConstraintValidator<EventValidation, EventRequestDto> {

    private int titleMaxSize;

    @Override
    public void initialize(EventValidation constraintAnnotation) {
        titleMaxSize = constraintAnnotation.titleMaxSize();
    }

    @Override
    public boolean isValid(EventRequestDto value, ConstraintValidatorContext context) throws RuntimeException {
        var title = value.getTitle();
        if (title.isEmpty()) throw new IllegalArgumentException(ErrorMessage.EMPTY_FIELD);
        if (title.length() > titleMaxSize)
            throw new IllegalArgumentException(ErrorMessage.REACHED_MAX_SIZE + titleMaxSize);
        EventDaysValidator.checkEventDays(value.getEventDays());
        return true;
    }

    private static class EventDaysValidator {

        static boolean checkEventDays(List<EventDayDto> eventDays) {
            var dates = eventDays.stream().map(EventDayDto::getEventDate).toList();
            var startTimes = eventDays.stream().map(EventDayDto::getEventStartTime).toList();
            var endTimes = eventDays.stream().map(EventDayDto::getEventEndTime).toList();
            if (haveEmptyDateFields(dates))
                throw new IllegalArgumentException(ErrorMessage.EMPTY_FIELD + "event days");
            if (haveDateDuplicates(dates))
                throw new IllegalArgumentException(ErrorMessage.DATE_EXISTS_IN_EVENTS_DATES);
            if (hasEmptyTimeFields(startTimes) || hasEmptyTimeFields(endTimes))
                throw new IllegalArgumentException(ErrorMessage.EMPTY_FIELD + "start and end time");
            if (hasDateInPast(dates))
                throw new IllegalArgumentException(ErrorMessage.EVENT_DAY_IN_PAST);
            return true;
        }

        private static boolean haveDateDuplicates(List<LocalDate> eventDays) {
            return eventDays
                    .stream()
                    .distinct().count() < eventDays.size();
        }

        private static boolean haveEmptyDateFields(List<LocalDate> eventDays) {
            return eventDays
                    .stream()
                    .anyMatch(Objects::isNull);
        }

        private static boolean hasEmptyTimeFields(List<LocalTime> localTimes) {
            return localTimes.stream().anyMatch(Objects::isNull);
        }

        private static boolean hasDateInPast(List<LocalDate> eventDays) {
            return eventDays.stream().anyMatch(e -> e.isBefore(LocalDate.now()));
        }
    }
}
