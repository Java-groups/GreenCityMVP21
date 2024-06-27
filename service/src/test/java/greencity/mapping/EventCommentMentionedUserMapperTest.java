package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentMentionedUserDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class EventCommentMentionedUserMapperTest {
    @InjectMocks
    EventCommentMentionedUserMapper mapper;

    @Test
    void convert() {
        User user = ModelUtils.getUser(1L);
        EventCommentMentionedUserDto expected = EventCommentMentionedUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        assertEquals(expected, mapper.convert(user));
    }

    @Test
    void mapAllToList() {
        User user = ModelUtils.getUser(1L);
        EventCommentMentionedUserDto expected = EventCommentMentionedUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        assertEquals(List.of(expected), mapper.mapAllToList(Set.of(user)));
    }
}