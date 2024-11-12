package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FriendsService {
    List<UserManagementDto> findFriends(Long userId);

    PageableAdvancedDto<UserFriendDto> findFriends(String name, Long userId, Pageable page);

    PageableAdvancedDto<UserFriendDto> findFriendRequests(Long userId, Pageable page);

    PageableAdvancedDto<UserFriendDto> findNotFriendsYet(String name, Long userId, Pageable page);

    void acceptFriendRequest(Long userId, Long friendId);

    void sendFriendRequest(Long userId, Long friendId);

    void cancelFriendRequest(Long userId, Long friendId);

    void declineFriendRequest(Long userId, Long friendId);
}
