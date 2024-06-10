package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@EqualsAndHashCode(exclude = {"attenders", "followers", "dates"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NonNull
    private String title;

    @Column
    private String titleImage;

    @ManyToOne
    private User organizer;

    @Column
    private LocalDate creationDate;

    @Column
    @NonNull
    private String description;
}
