package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name="locations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Address cannot be null")
    @NotEmpty(message = "Address cannot be empty")
    private String address;

    @NotNull(message = "Latitude cannot be null")
    @Min(value = -90, message = "Latitude must be greater than or equal to -90")
    @Max(value = 90, message = "Latitude must be less than or equal to 90")
    private Double lat;

    @NotNull(message = "Latitude cannot be null")
    @Min(value = -180, message = "Latitude must be greater than or equal to -180")
    @Max(value = 180, message = "Latitude must be less than or equal to 180")
    private Double lng;
}
