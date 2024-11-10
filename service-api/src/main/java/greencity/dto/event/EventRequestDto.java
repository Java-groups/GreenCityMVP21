package greencity.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventRequestDto {
    @NotNull
    @Schema(example = "1", description = "Unique identifier for the event")
    private Long id;

    @Schema(example = "Community Cleanup", description = "Title of the event")
    private String title;

    @Schema(example = "Join us for a community cleanup!", description = "Description of the event")
    private String description;

    @Size(max = 7)
    @Schema(description = "List of event days with start/end times and location info")
    private List<EventDayDto> eventDays = new ArrayList<>();

    @Schema(description = "Whether the event is open to the public", example = "true")
    private Boolean isOpenEvent;

    @Schema(description = "Whether the event lasts all day", example = "false")
    private Boolean allDayEvent;

    @Schema(hidden = true)
    private String image;


//    private List<String> additionalImages;
//    private List<String> tags;

}
