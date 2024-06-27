package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class EventCommentAuthorDtoMapperTest {
    @InjectMocks
    EventCommentAuthorDtoMapper mapper;

    @Test
    void convert() {
        User user = ModelUtils.getUser(1L);
        EventCommentAuthorDto expected = EventCommentAuthorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userProfilePicturePath(user.getProfilePicturePath())
                .build();
        assertEquals(expected, mapper.convert(user));
    }
}