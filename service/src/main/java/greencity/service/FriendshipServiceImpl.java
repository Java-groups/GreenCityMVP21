package greencity.service;

import greencity.dto.user.UserVO;
import greencity.entity.friend.Friendship;
import greencity.mapping.FriendshipMapper;
import greencity.repository.FriendshipRepo;
import greencity.utils.FriendUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {
    private static  final String STATUS_FRIEND = "FRIEND";
    private final FriendshipRepo friendshipRepo;
    private final FriendUtils friendUtils;

    @Override
    @Transactional
    public String addFriend(Long userId, Long friendId, UserVO user) {
        friendUtils.validateAdminOrCurrentUser(userId, user);
        friendUtils.validateUsersForFriendship(userId, friendId);

        friendUtils.checkFriendshipExistsOrThrow(userId, friendId);
        friendUtils.checkFriendshipExistsOrThrow(friendId, userId);

        friendshipRepo.save(FriendshipMapper.toEntity(userId, friendId, STATUS_FRIEND));
        friendshipRepo.save(FriendshipMapper.toEntity(friendId, userId, STATUS_FRIEND));

        return String.format("Friend by ID %d added successfully", friendId);
    }

    @Override
    @Transactional
    public String deleteFriend(Long userId, Long friendId, UserVO user) {
        friendUtils.validateAdminOrCurrentUser(userId, user);
        friendUtils.validateUsersForFriendship(userId, friendId);

        Friendship friendship1 = friendUtils.getFriendshipOrThrow(userId, friendId);
        Friendship friendship2 = friendUtils.getFriendshipOrThrow(friendId, userId);

        friendshipRepo.delete(friendship1);
        friendshipRepo.delete(friendship2);

        return String.format("Friend by ID %d has been successfully unfriended", friendId);
    }
}
