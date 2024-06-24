package greencity.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class EventCommentMessage {
    String authorName;
    String eventName;
    String commentAuthorName;
    LocalDateTime commentCreatedDateTime;
    String commentText;
    Long commentId;
}
