package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_comments")
public class EventComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 8000)
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @ManyToOne
    private EventComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventComment> childrenComments;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    @Column
    private boolean deleted;

    @Transient
    private boolean currentUserLiked = false;

    @ManyToMany
    @JoinTable(
            name = "event_comments_users_liked",
            joinColumns = @JoinColumn(name = "event_comment_id"),
            inverseJoinColumns = @JoinColumn(name = "users_liked_id")
    )
    private Set<User> likedUsers;
}
