package greencity.dto.subscriber;

import greencity.constant.ValidationConstant;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewsSubscriberVO implements Serializable {
    @Email(regexp = ValidationConstant.EMAIL_VALIDATION)
    private String email;
}