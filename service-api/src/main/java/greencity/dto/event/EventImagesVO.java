package greencity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventImagesVO {
    private Long id;

    @NonNull
    private String link;

    private EventVO event;
}
