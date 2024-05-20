package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.CommentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EcoNewsCommentDtoMapperTest {

    @InjectMocks
    EcoNewsCommentDtoMapper mapper;

    @Test
    void convert() {

        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        EcoNewsComment ecoNewsComment = EcoNewsComment.builder()
                .id(1L)
                .text("Sample Comment")
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .user(user)
                .comments(new ArrayList<>())
                .usersLiked(new HashSet<>())
                .deleted(false)
                .build();


        EcoNewsCommentDto dto = mapper.convert(ecoNewsComment);

        assert dto != null;
        assertEquals(dto.getId(), ecoNewsComment.getId());
        assertEquals(dto.getModifiedDate(), ecoNewsComment.getModifiedDate());
        assertEquals(dto.getAuthor().getId(), user.getId());
        assertEquals(dto.getAuthor().getName(), user.getName());
        assertEquals(dto.getText() , ecoNewsComment.getText());
        assertEquals(dto.getReplies(),ecoNewsComment.getComments().size());
        assertEquals(dto.getLikes(),ecoNewsComment.getUsersLiked().size());
        assertEquals(dto.isCurrentUserLiked(),ecoNewsComment.isCurrentUserLiked());
        assertEquals(dto.getStatus(), CommentStatus.EDITED);

    }
}