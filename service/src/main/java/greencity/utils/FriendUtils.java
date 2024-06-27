package greencity.utils;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.friend.Friendship;
import greencity.entity.friend.FriendshipId;
import greencity.enums.Role;
import greencity.exception.exceptions.DuplicateDataException;
import greencity.exception.exceptions.InvalidDataException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FriendshipRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FriendUtils {
    private final UserRepo userRepo;
    private final FriendshipRepo friendshipRepo;

    /**
     * Validates if the current user is either an admin or the user with the specified ID.
     *
     * @param userId the ID of the user to validate.
     * @param user the current user.
     * @throws AccessDeniedException if the current user is not an admin or the user with the specified ID.
     * @author Dmytro.Koval
     */
    public void validateAdminOrCurrentUser(Long userId, UserVO user) {
        if (user.getRole() != Role.ROLE_ADMIN && !userId.equals(user.getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED_MESSAGE);
        }
    }

    /**
     * Validates the existence and correctness of the provided user IDs for friendship.
     *
     * @param userId the ID of the user.
     * @param friendId the ID of the potential friend.
     * @throws NotFoundException if either user ID does not exist.
     * @throws InvalidDataException if the user ID and friend ID are the same.
     * @author Dmytro.Koval
     */
    public void validateUsersForFriendship(Long userId, Long friendId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        userRepo.findById(friendId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId));

        if (userId.equals(friendId)) {
            throw new InvalidDataException("User ID and Friend ID cannot be the same");
        }
    }

    /**
     * Checks if there is an existing friendship between the users, throwing an exception if not.
     *
     * @param userId the ID of the user.
     * @param friendId the ID of the friend.
     * @return the Friendship entity if found.
     * @throws NotFoundException if the friendship is not found between the specified users.
     * @author Dmytro.Koval
     */
    public Friendship getFriendshipOrThrow(Long userId, Long friendId) {
        return friendshipRepo.findById(new FriendshipId(userId, friendId))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Friendship not found between user %d and friend %d", userId, friendId)));
    }

    /**
     * Checks if there is already an existing friendship between the users, throwing an exception if so.
     *
     * @param userId the ID of the user.
     * @param friendId the ID of the friend.
     * @throws DuplicateDataException if a friendship already exists between the specified users.
     * @author Dmytro.Koval
     */
    public void checkFriendshipExistsOrThrow(Long userId, Long friendId) {
        boolean friendshipExists = friendshipRepo.existsById(new FriendshipId(userId, friendId));
        if (friendshipExists) {
            throw new DuplicateDataException(
                    String.format("Friendship already exists between users %d and %d", userId, friendId));
        }
    }
}
