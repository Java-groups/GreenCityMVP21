package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
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
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final EventRepo eventRepo;
    private final EventCommentRepo eventCommentRepo;
    private final UserService userService;
    private final RestClient restClient;
    private ModelMapper modelMapper;

    /**
     * Method to save {@link EventComment}.
     *
     * @param eventId                   id of {@link Event} to
     *                                    which we save comment.
     * @param requestDto dto with
     *                                    {@link EventComment}
     *                                    text, parentCommentId.
     * @param user                      {@link User} that saves the comment.
     * @return {@link EventCommentResponseDto} instance.
     */
    @Override
    public EventCommentResponseDto save(Long eventId, EventCommentRequestDto requestDto, UserVO user) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        EventComment eventComment = modelMapper.map(requestDto, EventComment.class);
        eventComment.setEvent(event);
        eventComment.setUser(modelMapper.map(user, User.class));

        setParentComment(eventId, eventComment, requestDto);

        eventComment.setStatus(CommentStatus.ORIGINAL);
        eventComment = eventCommentRepo.save(eventComment);
        return modelMapper.map(eventComment, EventCommentResponseDto.class);
    }

    private void setParentComment(Long eventId, EventComment eventComment, EventCommentRequestDto requestDto) {
        if (requestDto.getParentCommentId() != null && requestDto.getParentCommentId() > 0) {
            Long parentCommentId = requestDto.getParentCommentId();
            EventComment parentEventComment = eventCommentRepo
                    .findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED)
                    .orElseThrow(() ->
                            new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId));

            if (!parentEventComment.getEvent().getId().equals(eventId)) {
                throw new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId
                        + " in event with id: " + eventId);
            }

            if (parentEventComment.getParentComment() != null) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }

            eventComment.setParentComment(parentEventComment);
        } else if (requestDto.getParentCommentId() == null) {
            eventComment.setParentComment(null);
        }
    }

    /**
     * Method to count not deleted comments to certain
     * {@link Event}.
     *
     * @param eventId to specify {@link Event}
     * @return amount of comments
     */
    @Override
    public int countOfComments(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return eventCommentRepo.countByEvent(event);
    }

    /**
     * Method returns all comments to certain event specified by eventId.
     *
     * @param userVO    current {@link User}
     * @param eventId specifies {@link Event} to which we
     *                  search for comments
     * @return all comments to certain event specified by eventId.
     */
    @Override
    public PageableDto<EventCommentResponseDto> getAllEventComments(Pageable pageable, Long eventId, UserVO userVO) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        Page<EventComment> pages = eventCommentRepo.findAllByEventOrderByCreatedDateDesc(event, pageable);
        List<EventCommentResponseDto> eventComments = pages.stream()
                .map(eventComment -> modelMapper.map(eventComment, EventCommentResponseDto.class))
                .toList();
        return new PageableDto<>(
                eventComments,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }

    /**
     * Updates the text of an existing event comment.
     * This method updates the text of an existing event comment specified by its ID.
     * The comment must not be deleted, and the user attempting the update must be the owner of the comment.
     *
     * @param commentId   the ID of the comment to be updated
     * @param commentText the new text for the comment; must be between 1 and 8 characters
     * @param email       the email of the user attempting to update the comment
     */
    @Transactional
    @Override
    public void update(Long commentId, String commentText, String email) {
        EventComment comment = eventCommentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId));

        UserVO currentUser = userService.findByEmail(email);

        if (!currentUser.getId().equals(comment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        comment.setText(commentText);
        comment.setStatus(CommentStatus.EDITED);
        eventCommentRepo.save(comment);
    }

    /**
     * Method set 'DELETED' for field 'status' of the comment {@link EventComment} by id.
     *
     * @param eventCommentId specifies {@link EventComment} to which we search for comments.
     */
    @Transactional
    @Override
    public String delete(Long eventCommentId, String email) {
        EventComment eventComment = eventCommentRepo.findByIdAndStatusNot(eventCommentId, CommentStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + eventCommentId));

        UserVO currentUser = restClient.findByEmail(email);

        if (!currentUser.getId().equals(eventComment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        eventComment.setStatus(CommentStatus.DELETED);
        if (eventComment.getComments() != null) {
            eventComment.getComments()
                    .forEach(comment -> comment.setStatus(CommentStatus.DELETED));
        }

        eventCommentRepo.save(eventComment);
        return "Comment deleted successfully";
    }
}
