package greencity.dto.subscriber;

import greencity.constant.ValidationConstant;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewsSubscriberDto {
    @Email(regexp = ValidationConstant.EMAIL_VALIDATION)
    private String email;

    public static NewsSubscriberVO toNewsSubscriberVO(NewsSubscriberDto newsSubscriber) {
        return NewsSubscriberVO.builder().email(newsSubscriber.email).build();
    }
}
