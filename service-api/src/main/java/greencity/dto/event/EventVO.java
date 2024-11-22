package greencity.dto.event;

import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.enums.EventType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventVO {
    private Long id;

    @NotNull
    private String title;

    private UserVO organizer;

    private List<EventDayVO> eventDays = new ArrayList<>();

    private String image;

    @Size(max = 4)
    private List<EventImagesVO> additionalImages = new ArrayList<>();

    private Set<UserVO> attendants = new HashSet<>();

    private List<TagVO> tags;

    private EventType type;

    @NotNull
    private String description;

    private Boolean isOpen = true;
}
