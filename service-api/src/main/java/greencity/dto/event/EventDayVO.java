package greencity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDayVO {
    private Long id;

    private LocalDate eventDate;

    private LocalTime eventStartTime;

    private LocalTime eventEndTime;

    private EventVO event;

    private Double latitude;

    private Double longitude;

    private Boolean isOnline;

    private String onlineLink;
}
