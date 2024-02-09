package greencity.dto.habit;

import greencity.constant.AppConstant;
import greencity.enums.HabitAssignStatus;
import lombok.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignManagementDto {
    @NotNull
    private Long id;
    @NotNull
    private HabitAssignStatus status;
    @NotEmpty
    private ZonedDateTime createDateTime;
    @NotNull
    private Long habitId;
    @NotNull
    private Long userId;

    @Min(AppConstant.MIN_DAYS_DURATION)
    @Max(AppConstant.MAX_DAYS_DURATION)
    @NotNull
    private Integer duration;

    @NotNull
    private Integer workingDays;
    @NotNull
    private Integer habitStreak;
    @NotEmpty
    private ZonedDateTime lastEnrollment;
    private Boolean progressNotificationHasDisplayed;
}
