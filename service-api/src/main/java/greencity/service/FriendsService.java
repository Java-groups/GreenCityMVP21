package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface FriendsService {

    List<UserManagementDto> findFriends(Long userId);

    public void acceptFriendRequest(Long userId, Long friendId);

    PageableAdvancedDto<UserFriendDto> findFriends(String name, Long userId, Pageable page);

    PageableAdvancedDto<UserFriendDto> findFriendRequests(Long userId, Pageable page);

    PageableAdvancedDto<UserFriendDto> findNotFriendsYet(String name, Long userId, Pageable page);
}
