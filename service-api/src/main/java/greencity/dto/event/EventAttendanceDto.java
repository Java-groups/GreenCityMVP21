package greencity.dto.event;

import greencity.enums.Role;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class EventAttendanceDto {
    private Long id;
    private String firstName;
    private String name;
    private Role role;
    private String imagePath;
}
