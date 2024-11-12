package greencity.entity;

import greencity.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventDay> eventDays = new ArrayList<>();

    private String image;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Size(max = 4)
    private List<EventImages> additionalImages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType type;

    @NotNull
    private String description;

    @Column(name = "is_open")
    private Boolean isOpen = true;
}
//    @ManyToMany
//    @JoinTable(name = "events_attendants",
//        joinColumns = @JoinColumn(name = "event_id"),
//        inverseJoinColumns = @JoinColumn(name = "user_id"))
//    private Set<User> attendants = new HashSet<>();
//
//    @ManyToMany
//    @JoinTable(name = "events_tags",
//            joinColumns = @JoinColumn(name = "event_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id"))
//    private List<Tag> tags;