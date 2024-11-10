package greencity.mapping.event;

import greencity.dto.event.EventDayDto;
import greencity.entity.EventDay;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventDayDtoMapper extends AbstractConverter<EventDayDto, EventDay> {

    @Override
    protected EventDay convert(EventDayDto source) {
        return EventDay.builder()
                .id(source.getId())
                .eventDate(source.getEventDate())
                .eventStartTime(source.getEventStartTime())
                .eventEndTime(source.getEventEndTime())
                .longitude(source.getLongitude())
                .latitude(source.getLatitude())
                .build();
    }
}
