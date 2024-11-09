package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import org.springframework.data.domain.Pageable;

public interface FriendsService {
    public PageableAdvancedDto<UserFriendDto> findFriends(String name, Long userId, Pageable page);
    public PageableAdvancedDto<UserFriendDto> findFriendRequests(Long userId, Pageable page);
    public PageableAdvancedDto<UserFriendDto> findNotFriendsYet(String name, Long userId, Pageable page);
}
