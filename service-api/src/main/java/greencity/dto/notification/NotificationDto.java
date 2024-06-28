package greencity.dto.notification;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    @NonNull
    private Long id;

    @NonNull
    private NotificationUserDto user;

    @NonNull
    private NotificationUserDto sender;

    @NonNull
    private String section;

    private String message;

    @NonNull
    private LocalDateTime timestamp;

    private boolean isRead;
}
