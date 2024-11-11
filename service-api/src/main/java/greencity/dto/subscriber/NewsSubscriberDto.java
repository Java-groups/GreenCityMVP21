package greencity.dto.subscriber;

import greencity.constant.ValidationConstant;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewsSubscriberDto {
    @Email(regexp = ValidationConstant.EMAIL_VALIDATION)
    private String email;

    public static NewsSubscriberVO toNewsSubscriberVO(NewsSubscriberDto newsSubscriber) {
        return NewsSubscriberVO.builder().email(newsSubscriber.email.toLowerCase())
                .unsubscribeToken(UUID.randomUUID().toString()).build();
    }
}
