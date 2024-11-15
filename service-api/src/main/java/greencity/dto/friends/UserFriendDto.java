package greencity.dto.friends;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserFriendDto {
    private Long id;
    private String name;
    private String city;
    private Double rating;
    private Long mutualFriends;
    private String profilePicturePath;
    private Long chatId;
    private String email;
    private String friendStatus;
}
