package greencity.entity.friend;

import jakarta.persistence.Embeddable;

@Embeddable
public record FriendshipRequestId(Long userId, Long friendId){}
