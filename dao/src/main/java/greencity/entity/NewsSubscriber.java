package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.io.Serializable;

import static greencity.constant.ValidationConstants.VALIDATION_EMAIL;


@Entity
@Table(name = "subscribers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewsSubscriber implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    @Email(regexp = VALIDATION_EMAIL)
    private String email;
    @Column(nullable = false, unique = true)
    private String unsubscribeToken;
}
