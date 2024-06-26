package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @Mock
    UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

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
        User user = ModelUtils.getUser(1L);
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
    void save() {
        Long eventId = 1L;
        EventCommentRequestDto requestDto = getEventCommentRequestDto();
        UserVO userVO = getUserVO();
        Event event = getEvent();
        EventComment eventComment = getEventComment();
        User user = getUser();
        EventCommentResponseDto responseDto = getEventCommentResponseDto().setMentionedUsers(new ArrayList<>());
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.map(requestDto, EventComment.class)).thenReturn(eventComment);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        eventComment.setMentionedUsers(new HashSet<>());
        when(eventCommentRepo.save(eventComment)).thenReturn(eventComment);
        when(modelMapper.map(eventComment, EventCommentResponseDto.class)).thenReturn(responseDto);
        assertEquals(responseDto, eventCommentService.save(eventId, requestDto, userVO));
    }

    @Test
    void saveWithMentionedUser() {
        Long eventId = 1L;
        EventCommentRequestDto requestDto = getEventCommentRequestDto();
        requestDto.setText("text @username");
        UserVO userVO = getUserVO();
        Event event = getEvent();
        EventComment eventComment = getEventComment();
        User user = getUser();
        User mentionedUser = User.builder().id(2L).name("username").build();
        EventCommentResponseDto responseDto = getEventCommentResponseDto().setMentionedUsers(new ArrayList<>());
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.map(requestDto, EventComment.class)).thenReturn(eventComment);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(userRepo.findByName("username")).thenReturn(Optional.of(mentionedUser));
        when(eventCommentRepo.save(eventComment)).thenReturn(eventComment);
        when(modelMapper.map(eventComment, EventCommentResponseDto.class)).thenReturn(responseDto);
        assertEquals(responseDto, eventCommentService.save(eventId, requestDto, userVO));
    }

    @Test
    void saveThrowsNotFound() {
        Long eventId = 0L;
        EventCommentRequestDto requestDto = getEventCommentRequestDto();
        UserVO userVO = getUserVO();
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.save(eventId, requestDto, userVO));
    }

    @Test
    void countOfComments() {
        Long eventId = 1L;
        Event event = getEvent();
        int expected = 5;
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.countByEvent(event)).thenReturn(expected);
        assertEquals(expected, eventCommentService.countOfComments(eventId));
    }

    @Test
    void countOfCommentsThrowsNotFound() {
        Long eventId = 0L;
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.countOfComments(eventId));
    }

    @Test
    void getAllEventComments() {
        Long eventId = 1L;
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Event event = getEvent();
        EventComment comment = getEventComment();
        EventCommentResponseDto responseDto = getEventCommentResponseDto();
        List<EventComment> expectedComments = List.of(comment);
        List<EventCommentResponseDto> expectedCommentsDto = List.of(responseDto);
        Page<EventComment> translationPage = new PageImpl<>(expectedComments,
                pageable, expectedComments.size());
        PageableDto<EventCommentResponseDto> expected = new PageableDto<>(expectedCommentsDto, expectedCommentsDto.size(), 0, 1);
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findAllByEventOrderByCreatedDateDesc(event, pageable)).thenReturn(translationPage);
        when(modelMapper.map(comment, EventCommentResponseDto.class)).thenReturn(responseDto);
        assertEquals(expected, eventCommentService.getAllEventComments(pageable, eventId));
    }

    @Test
    void getAllEventCommentsThrowsNotFound() {
        Long eventId = 0L;
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.getAllEventComments(pageable, eventId));
    }

    @Test
    void getByEventCommentId() {
        Long eventId = 1L;
        Long commentId = 1L;
        Event event = getEvent();
        EventComment comment = getEventComment();
        EventCommentResponseDto expected = getEventCommentResponseDto();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, EventCommentResponseDto.class)).thenReturn(expected);
        assertEquals(expected, eventCommentService.getByEventCommentId(eventId, commentId));
    }

    @Test
    void getByEventCommentIdThrowsNotFoundEvent() {
        Long eventId = 0L;
        Long commentId = 1L;
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.getByEventCommentId(eventId, commentId));
    }

    @Test
    void getByEventCommentIdThrowsNotFoundComment() {
        Long eventId = 1L;
        Long commentId = 0L;
        Event event = getEvent();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.getByEventCommentId(eventId, commentId));
    }

    @Test
    void getByEventCommentIdThrowsBadRequest() {
        Long eventId = 1L;
        Long commentId = 1L;
        Event event = getEvent();
        EventComment comment = EventComment.builder()
                .id(commentId)
                .event(Event.builder()
                        .id(2L)
                        .build())
                .build();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        assertThrows(BadRequestException.class, () -> eventCommentService.getByEventCommentId(eventId, commentId));
    }
}
