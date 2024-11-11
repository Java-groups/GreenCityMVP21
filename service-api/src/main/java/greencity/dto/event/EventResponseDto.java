package greencity.dto.event;

import greencity.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private Boolean isOpen;
    private EventType type;
    private String image;
    private List<EventDayDto> dayList;
    private List<String> additionalImages;
}
