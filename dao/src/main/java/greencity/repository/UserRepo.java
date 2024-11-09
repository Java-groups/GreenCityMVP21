package greencity.repository;

import greencity.dto.habit.HabitVO;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.repository.options.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Find {@link User} by email.
     *
     * @param email user email.
     * @return {@link User}
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all {@link UserManagementVO}.
     *
     * @param filter   filter parameters
     * @param pageable pagination
     * @return list of all {@link UserManagementVO}
     */
    @Query(" SELECT new greencity.dto.user.UserManagementVO(u.id, u.name, u.email, u.userCredo, u.role, u.userStatus) "
        + " FROM User u ")
    Page<UserManagementVO> findAllManagementVo(UserFilter filter, Pageable pageable);

    /**
     * Find not 'DEACTIVATED' {@link User} by email.
     *
     * @param email - {@link User}'s email
     * @return found {@link User}
     * @author Vasyl Zhovnir
     */
    @Query("FROM User WHERE email=:email AND userStatus <> 1")
    Optional<User> findNotDeactivatedByEmail(String email);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     * @author Zakhar Skaletskyi
     */
    @Query("SELECT id FROM User WHERE email=:email")
    Optional<Long> findIdByEmail(String email);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link User}'s id
     * @param userLastActivityTime - new {@link User}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastActivityTime = :userLastActivityTime WHERE u.id = :userId")
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * Updates user status for a given user.
     *
     * @param userId     - {@link User}'s id
     * @param userStatus {@link String} - string value of user status to set
     */
    @Modifying
    @Transactional
    @Query("UPDATE User SET userStatus = CASE "
        + "WHEN (:userStatus = 'DEACTIVATED') THEN 1 "
        + "WHEN (:userStatus = 'ACTIVATED') THEN 2 "
        + "WHEN (:userStatus = 'CREATED') THEN 3 "
        + "WHEN (:userStatus = 'BLOCKED') THEN 4 "
        + "ELSE 0 END "
        + "WHERE id = :userId")
    void updateUserStatus(Long userId, String userStatus);

    /**
     * Find the last activity time by {@link User}'s id.
     *
     * @param userId - {@link User}'s id
     * @return {@link Date}
     */
    @Query(nativeQuery = true,
        value = "SELECT last_activity_time FROM users WHERE id=:userId")
    Optional<Timestamp> findLastActivityTimeById(Long userId);

    /**
     * Updates user rating as event organizer.
     *
     * @param userId {@link User}'s id
     * @param rate   new {@link User}'s rating as event organizer
     * @author Danylo Hlynskyi
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User SET eventOrganizerRating=:rate WHERE id=:userId")
    void updateUserEventOrganizerRating(Long userId, Double rate);

    /**
     * Retrieves the list of the user's friends (which have INPROGRESS assign to the
     * habit).
     *
     * @param habitId {@link HabitVO} id.
     * @param userId  {@link UserVO} id.
     * @return List of friends.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM ((SELECT user_id FROM users_friends AS uf "
        + "WHERE uf.friend_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.user_id "
        + "AND ha.status = 'INPROGRESS') = 1) "
        + "UNION "
        + "(SELECT friend_id FROM users_friends AS uf "
        + "WHERE uf.user_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.friend_id "
        + "AND ha.status = 'INPROGRESS') = 1)) as ui JOIN users as u ON user_id = u.id")
    List<User> getFriendsAssignedToHabit(Long userId, Long habitId);

    /**
     * Get all user friends.
     *
     * @param userId The ID of the user.
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN ( "
        + "(SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')"
        + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'));")
    List<User> getAllUserFriends(Long userId);

    /**
     * Get all user friends.
     *
     * @param name for search if partially exists in user.name
     * @param userId The ID of the user.
     * @param pageable pagination
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT u.* FROM users u JOIN users_friends uf "
            + "ON u.id = uf.friend_id OR u.id = uf.user_id "
            + "WHERE u.name LIKE CONCAT('%', :name, '%') "
            + "AND ((u.id = uf.friend_id AND uf.user_id = :userId) "
            + "OR (u.id = uf.user_id AND uf.friend_id = :userId))")
    Page<User> getAllUserFriends(String name, Long userId, Pageable pageable);

    /**
     * Get user and his friend mutual friends count.
     *
     * @param userId The ID of the user
     * @param friendId The ID of user's friend
     *
     * @return Long
     */
    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM ("
            + "SELECT user_id FROM ("
            + "SELECT friend_id AS user_id FROM users_friends "
            + "WHERE user_id = :userId "
            + "UNION "
            + "SELECT user_id AS user_id FROM users_friends "
            + "WHERE friend_id = :userId) AS user1_friends "
            + "INTERSECT "
            + "SELECT user_id FROM ("
            + "SELECT friend_id AS user_id FROM users_friends "
            + "WHERE user_id = :friendId "
            + "UNION "
            + "SELECT user_id AS user_id FROM users_friends "
            + "WHERE friend_id = :friendId) AS user2_friends) "
            + "AS common_friends")
    Long getMutualFriendsCount(Long userId, Long friendId);

    /**
     * Get all user friends.
     *
     * @param userId The ID of the user.
     * @param pageable pagination
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT u.* FROM users u JOIN friends_requests fr "
            + "ON u.id = fr.user_id WHERE fr.friend_id  = :userId")
    Page<User> getAllUserFriendsRequests(Long userId, Pageable pageable);
}
