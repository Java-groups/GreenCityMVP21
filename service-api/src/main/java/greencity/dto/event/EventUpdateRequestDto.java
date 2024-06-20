package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.annotations.ValidEventDayInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequestDto {
    @NotNull
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(message = "Title must be a maximum of {max} characters", max = 70)
    private String title;

    @ValidEventDayInfo
    @Size(max = 7, message = "Event must have not more than {max} days")
    private List<EventResponseDayInfoDto> dateTimes;

    @NotBlank
    @Size(min = 20, max = 63206, message = "Description must be at least {min} and maximum {max} characters")
    private String description;

    private List<EventImageDto> images;

    private List<String> tagNames;

    @JsonProperty(value = "open")
    private Boolean isOpen;
}
