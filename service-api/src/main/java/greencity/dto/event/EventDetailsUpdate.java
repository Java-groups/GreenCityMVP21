package greencity.dto.event;

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
public class EventDetailsUpdate {
    @NotNull
    @Schema(example = "1", description = "Unique identifier for the event")
    private Long id;

    @Schema(example = "Community Cleanup", description = "Title of the event")
    @NotBlank
    private String title;

    @Schema(example = "Join us for a community cleanup!", description = "Description of the event")
    @Size(min = 20, max = 63206)
    @NotBlank
    private String description;

    @Schema(description = "List of event days with start/end times and location info")
    @Size(max = 7)
    private List<EventDayDto> eventDays = new ArrayList<>();

    @Schema(description = "Whether the event is open to the public", example = "true")
    private Boolean isOpenEvent;

    @Schema(description = "Whether the event lasts all day", example = "false")
    private Boolean allDayEvent;

    @Schema(hidden = true)
    private String image;

    @Schema(hidden = true)
    @NotNull
    @Size(max = 4)
    private List<String> additionalImages;

    @Schema(example = "Environmental")
    private List<String> tags;
}
