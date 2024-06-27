package greencity.dto.event;

import greencity.enums.EventStatus;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSaveDayInfoDto {
    private boolean isAllDay;

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

    private int dayNumber;

    private EventStatus status;

    private String link;

    private EventAddressDto address;
}
