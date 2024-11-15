package greencity.validator;

import greencity.dto.event.EventDayDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class EventDaysValidator {

     public boolean checkEventDays(List<EventDayDto> eventDays) {
        var dates = eventDays.stream().map(EventDayDto::getEventDate).toList();
        var startTimes = eventDays.stream().map(EventDayDto::getEventStartTime).toList();
        var endTimes = eventDays.stream().map(EventDayDto::getEventEndTime).toList();
        if (haveEmptyDateFields(dates))
            return false;
        if (haveDateDuplicates(dates))
            return false;
        if (hasEmptyTimeFields(startTimes) || hasEmptyTimeFields(endTimes))
            return false;
        if (hasDateInPast(dates))
            return false;
        return true;
    }

     public boolean haveDateDuplicates(List<LocalDate> eventDays) {
        return eventDays
                .stream()
                .distinct().count() < eventDays.size();
    }

    public boolean haveEmptyDateFields(List<LocalDate> eventDays) {
        return eventDays
                .stream()
                .anyMatch(Objects::isNull);
    }

    public boolean hasEmptyTimeFields(List<LocalTime> localTimes) {
        return localTimes.stream().anyMatch(Objects::isNull);
    }

    public boolean hasDateInPast(List<LocalDate> eventDays) {
        return eventDays.stream().anyMatch(e -> e.isBefore(LocalDate.now()));
    }
}
