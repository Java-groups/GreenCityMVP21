package greencity.mapping.event;

import greencity.dto.event.EventResponseDto;
import greencity.entity.Event;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventResponseDtoMapper extends AbstractConverter<Event, EventResponseDto> {
    private final EventDayToDtoMapper mapper;

    @Override
    protected EventResponseDto convert(Event source) {
        return EventResponseDto.builder()
                .id(source.getId())
                .dayList(source.getEventDays().stream()
                        .map(mapper::convert)
                        .toList())
                .description(source.getDescription())
                .title(source.getTitle())
                .type(source.getType())
                .isOpen(source.getIsOpen())
                .image(source.getImage())
                .build();
    }
}
