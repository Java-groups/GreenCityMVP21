package greencity.mapping.event;

import greencity.dto.event.EventDayDto;
import greencity.entity.EventDay;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventDayToDtoMapper extends AbstractConverter<EventDay, EventDayDto> {
    @Override
    protected EventDayDto convert(EventDay source) {
        return EventDayDto.builder()
                .id(source.getId())
                .eventDate(source.getEventDate())
                .eventStartTime(source.getEventStartTime())
                .eventEndTime(source.getEventEndTime())
                .latitude(source.getLatitude())
                .longitude(source.getLongitude())
                .build();
    }
}
