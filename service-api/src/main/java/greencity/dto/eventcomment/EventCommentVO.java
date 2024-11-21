package greencity.dto.eventcomment;

import greencity.dto.event.EventVO;
import greencity.dto.user.UserVO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventCommentVO {
    private Long id;

    @Size(min = 1, max = 8000)
    private String comment;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private EventCommentVO parentComment;

    private List<EventCommentVO> childrenComments = new ArrayList<>();

    private UserVO user;

    private EventVO event;

    private boolean deleted;

    private boolean currentUserLiked = false;

    private Set<UserVO> likedUsers = new HashSet<>();
}
