package greencity.dto.location;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class LocationDto {
    @NonNull
    private Long id;

    @NonNull
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
