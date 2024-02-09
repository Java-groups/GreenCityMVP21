package greencity.dto.habit;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import lombok.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class AddCustomHabitDtoResponse {
    private Long id;
    private Long userId;
    private String image;
    @Min(value = 1, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    @Max(value = 3, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    @NotNull(message = ServiceValidationConstants.HABIT_COMPLEXITY)
    private Integer complexity;
    private Integer defaultDuration;
    private List<CustomShoppingListItemResponseDto> customShoppingListItemDto;
    private List<HabitTranslationDto> habitTranslations;
    @Valid
    @Size(min = 1, message = ServiceValidationConstants.TAG_LIST_MIN_LENGTH)
    @Size(max = 3, message = ServiceValidationConstants.TAG_LIST_MAX_LENGTH)
    private Set<Long> tagIds;
}
