package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;  // Замість jakarta.transactionа
import org.springframework.data.domain.Pageable;

public interface FriendsService {

    List<UserManagementDto> findFriends(Long userId);

    void acceptFriendRequest(Long userId, Long friendId);

    PageableAdvancedDto<UserFriendDto> findFriends(String name, Long userId, Pageable page);

    PageableAdvancedDto<UserFriendDto> findFriendRequests(Long userId, Pageable page);

    PageableAdvancedDto<UserFriendDto> findNotFriendsYet(String name, Long userId, Pageable page);

    @Transactional
    void sendFriendRequest(Long userId, Long friendId);

    @Transactional
    void cancelFriendRequest(Long userId, Long friendId);

    @Transactional
    void declineFriendRequest(Long userId, Long friendId);
}
