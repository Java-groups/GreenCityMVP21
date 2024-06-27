package greencity.entity.friend;

import greencity.enums.FriendshipRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendship_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipRequest {
    @EmbeddedId
    private FriendshipRequestId pk;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendshipRequestStatus status;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    public Long getUserId() {
        return pk.userId();
    }

    public Long getFriendId() {
        return pk.friendId();
    }
}
