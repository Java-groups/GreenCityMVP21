package greencity.service;

import greencity.dto.subscriber.NewsSubscriberVO;

import java.util.List;

public interface SubscriberService {
    /**
     * Save NewsSubscriberVO to DB.
     *
     * @param newsSubscriberVO - of NewsSubscriber.
     * @return saved {@link NewsSubscriberVO }.
     */
    NewsSubscriberVO save(NewsSubscriberVO newsSubscriberVO);

    /**
     * Method for finding NewsSubscriberVO by id.
     *
     * @param id - NewsSubscriberVO's id.
     * @return NewsSubscriberVO.
     */
    NewsSubscriberVO findById(Long id);

    /**
     * Method for finding all NewsSubscriberVOs.
     *
     * @return list of NewsSubscriberVOs.
     */
    List<NewsSubscriberVO> findAll();

    /**
     * Method for deleting NewsSubscriberVO by id.
     *
     * @param id - NewsSubscriberVO's id.
     * @return id of deleted NewsSubscriberVO.
     */
    Long deleteById(Long id);

    /**
     * Finds NewsSubscriberVO by name.
     *
     * @param email to find by.
     * @return a NewsSubscriberVO by email.
     */
    NewsSubscriberVO findByEmail(String email);

}
