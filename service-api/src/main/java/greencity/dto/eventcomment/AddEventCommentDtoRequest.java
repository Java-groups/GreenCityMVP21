package greencity.dto.eventcomment;

import greencity.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddEventCommentDtoRequest {
    @NotBlank(message = ErrorMessage.STRING_IS_BLANK)
    @Size(min = 1, max = 8000, message = ErrorMessage.STRING_SIZE_IS_INVALID)
    private String comment;
    private Long parentCommentId;
}
