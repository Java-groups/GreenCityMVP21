package greencity.service;

import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;

public interface EventCommentService {
    /**
     * Method to save {@link EventCommentVO}.
     *
     * @param eventId                   id of {@link EventVO} to which we save
     *                                  comment.
     * @param addEventCommentDtoRequest dto with {@link EventCommentVO} text,
     *                                  parentCommentId.
     * @param user                      {@link UserVO} that saves the comment.
     * @return {@link AddEventCommentDtoResponse} instance.
     */
    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
                                    UserVO user);

    /**
     * Method to update {@link EcoNewsCommentVO}.
     *
     * @param newText updated text of {@link EventCommentVO} to which we update
     *                comment.
     * @param eventId id of eventId {@link EventVO}
     * @param user    {@link UserVO} that saves the comment.
     */
    void update(String newText, long eventId, UserVO user);
}
