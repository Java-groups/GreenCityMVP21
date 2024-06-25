package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

public interface EventCommentService {
    /**
     * Method to save {@link EventComment}.
     *
     * @param eventId                   id of {@link Event} to which we save
     *                                    comment.
     * @param requestDto dto with {@link EventComment} text,
     *                                    parentCommentId.
     * @param user                        {@link UserVO} that saves the comment.
     * @return {@link EventCommentResponseDto} instance.
     */
    EventCommentResponseDto save(Long eventId, EventCommentRequestDto requestDto, UserVO user);

    int countOfComments(Long eventId);

    PageableDto<EventCommentResponseDto> getAllEventComments(Pageable pageable, Long eventId);

    EventCommentResponseDto getByEventCommentId(Long eventId, Long commentId);

    /**
     * Method for updating the {@link EventComment} by id.
     *
     * @param commentId   - {@link EventComment} instance id which will be updated.
     * @param commentText - {@link EventComment} text to update.
     * @param email           User who wants to update a comment.
     */
    void update(Long eventId, Long commentId, String commentText, String email);

    /**
     * Method for deleting the {@link EventComment} by id.
     *
     * @param eventCommentId - {@link EventComment} instance id which will be deleted.
     * @param email           User who wants to delete a comment.
     */
    String delete(Long eventId, Long eventCommentId, String email);
}
