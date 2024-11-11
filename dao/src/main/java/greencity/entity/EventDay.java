package greencity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "event_days")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@EqualsAndHashCode
public class EventDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime eventStartTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime eventEndTime;

    @ManyToOne
    private Event event;

    private Double latitude;
    private Double longitude;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "online_link")
    private String onlineLink;
}
