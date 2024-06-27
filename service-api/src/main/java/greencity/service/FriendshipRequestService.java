package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.FriendshipRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.FriendshipRequestStatus;
import org.springframework.data.domain.Pageable;

public interface FriendshipRequestService {
    PageableDto<FriendshipRequestDto> getFriendshipRequests(Long userId, Pageable pageable);

    String updateFriendshipRequest(Long userId, Long friendId, FriendshipRequestStatus status, UserVO user);
}
