package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class EventCommentResponseDtoMapperTest {
    @InjectMocks
    EventCommentResponseDtoMapper mapper;

    @Test
    void convert() {
        EventCommentResponseDto expected = ModelUtils.getEventCommentResponseDto();
        EventComment comment = EventComment.builder().id(expected.getId())
                .text(expected.getText())
                .createdDate(expected.getCreatedDate())
                .modifiedDate(expected.getModifiedDate())
                .user(User.builder()
                        .id(expected.getAuthor().getId())
                        .name(expected.getAuthor().getName())
                        .profilePicturePath(expected.getAuthor().getUserProfilePicturePath())
                        .build())
                .event(ModelUtils.getEvent())
                .mentionedUsers(Set.of(User.builder()
                        .id(expected.getMentionedUsers().getFirst().getId())
                        .name(expected.getMentionedUsers().getFirst().getName())
                        .build()))
                .build();
        assertEquals(expected, mapper.convert(comment));
    }

    @Test
    void mapAllToList() {
        EventCommentResponseDto expected = ModelUtils.getEventCommentResponseDto();
        EventComment comment = EventComment.builder().id(expected.getId())
                .text(expected.getText())
                .createdDate(expected.getCreatedDate())
                .modifiedDate(expected.getModifiedDate())
                .user(User.builder()
                        .id(expected.getAuthor().getId())
                        .name(expected.getAuthor().getName())
                        .profilePicturePath(expected.getAuthor().getUserProfilePicturePath())
                        .build())
                .event(ModelUtils.getEvent())
                .mentionedUsers(Set.of(User.builder()
                        .id(expected.getMentionedUsers().getFirst().getId())
                        .name(expected.getMentionedUsers().getFirst().getName())
                        .build()))
                .build();
        assertEquals(List.of(expected), mapper.mapAllToList(List.of(comment)));
    }
}