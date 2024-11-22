package greencity.service;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.subscriber.NewsSubscriberVO;
import greencity.dto.user.UserVO;

import java.util.List;

public interface EmailService {

    /**
     * Method sends eco news for all subscribed users.
     */
    void sendNewNewsForSubscriber(List<NewsSubscriberVO> subscribers,
                                  AddEcoNewsDtoResponse newsDto);

    /**
     * Method sends a subscription confirmation email for a new subscriber from template
     */
    void sendConfirmationLetter(NewsSubscriberVO subscriber);

    void sendEventCommentNotification(UserVO eventOwner, EventCommentVO comment);
}
