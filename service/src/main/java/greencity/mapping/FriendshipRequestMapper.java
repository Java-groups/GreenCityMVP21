package greencity.mapping;

import greencity.entity.friend.FriendshipRequest;
import greencity.entity.friend.FriendshipRequestId;
import greencity.enums.FriendshipRequestStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class FriendshipRequestMapper {
    public static FriendshipRequest toEntity(
            Long userId,
            Long friendId,
            FriendshipRequestStatus status) {
        FriendshipRequest request = new FriendshipRequest();
        request.setPk(new FriendshipRequestId(userId, friendId));
        request.setStatus(status);
        request.setCreatedDate(LocalDateTime.now());
        return request;
    }
}
