package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventCommentServiceImplTest {

    @Mock
    private EventCommentRepo eventCommentRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventCommentServiceImpl eventCommentService;

    @Test
    void testUpdateCommentSuccess() {
        Long eventId = 1L;
        Long commentId = 1L;
        String editedText = "Updated Comment";
        String email = "email@email.com";

        UserVO currentUser = getUserVO();
        when(userService.findByEmail(email)).thenReturn(currentUser);
        EventComment eventComment = getEventComment();
        Event event = getEvent();

        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.ofNullable(eventComment));

        eventCommentService.update(eventId, commentId, editedText, email);

        assert eventComment != null;
        assertEquals(CommentStatus.EDITED, eventComment.getStatus());
    }

    @Test
    void updateCommentThatDoesntExistsThrowException() {
        Long eventId = 1L;
        Long commentId = 1L;
        String editedText = "Updated Comment";
        String email = "email@email.com";

        Event event = getEvent();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                        () -> eventCommentService.update(eventId, commentId, editedText, email));
        assertEquals(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId, notFoundException.getMessage());
    }

    @Test
    void updateCommentThatDoesntBelongsToUserThrowException() {
        User user = ModelUtils.getUser();
        UserVO currentUser = getUserVO();
        user.setId(2L);

        Long eventId = 1L;
        Long commentId = 1L;
        EventComment eventComment = getEventComment();
        eventComment.setUser(user);
        String editedText = "Updated text";
        String email = "email@email.com";
        when(userService.findByEmail(email)).thenReturn(currentUser);

        Event event = getEvent();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.of(eventComment));

        UserHasNoPermissionToAccessException userHasNoPermissionToAccessException =
                assertThrows(UserHasNoPermissionToAccessException.class,
                        () -> eventCommentService.update(eventId, commentId, editedText, email));
        assertEquals(ErrorMessage.USER_HAS_NO_PERMISSION, userHasNoPermissionToAccessException.getMessage());
    }

    @Test
    void save() {}

    @Test
    void saveThrowsNotFound() {}

    @Test
    void countOfComments() {}

    @Test
    void countOfCommentsThrowsNotFound() {}

    @Test
    void getAllEventComments() {}

    @Test
    void getAllEventCommentsThrowsNotFound() {}

    @Test
    void getByEventCommentId() {}

    @Test
    void getByEventCommentIdThrowsNotFoundEvent() {}

    @Test
    void getByEventCommentIdThrowsNotFoundComment() {}
}
