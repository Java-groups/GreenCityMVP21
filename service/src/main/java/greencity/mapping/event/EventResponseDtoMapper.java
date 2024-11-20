package greencity.mapping.event;

import greencity.dto.event.EventResponseDto;
import greencity.dto.tag.TagUaEnDto;
import greencity.entity.Event;
import greencity.entity.EventImages;
import greencity.entity.localization.TagTranslation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventResponseDtoMapper extends AbstractConverter<Event, EventResponseDto> {
    private final EventDayToDtoMapper mapper;

    @Override
    protected EventResponseDto convert(Event source) {
        List<TagUaEnDto> tagUaEnDtoList = new ArrayList<>();
        source.getTags().forEach(tag -> {
            List<TagTranslation> tagTranslations = tag.getTagTranslations();
            tagUaEnDtoList.add(TagUaEnDto.builder()
                    .nameEn(tagTranslations.stream()
                            .filter(lang -> lang.getLanguage().getCode().equals("en"))
                            .findFirst()
                            .orElseThrow()
                            .getName())
                    .nameUa(tagTranslations.stream()
                            .filter(lang -> lang.getLanguage().getCode().equals("ua"))
                            .findFirst()
                            .orElseThrow()
                            .getName())
                    .build());
        });
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
                .additionalImages(source.getAdditionalImages().stream()
                        .map(EventImages::getLink)
                        .toList())
                .tags(tagUaEnDtoList)
                .build();
    }
}
