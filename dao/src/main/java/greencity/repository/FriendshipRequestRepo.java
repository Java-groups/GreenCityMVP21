package greencity.repository;

import greencity.entity.friend.FriendshipRequest;
import greencity.entity.friend.FriendshipRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRequestRepo extends JpaRepository<FriendshipRequest, FriendshipRequestId> {}
