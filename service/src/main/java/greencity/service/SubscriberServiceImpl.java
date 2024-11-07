package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.subscriber.NewsSubscriberVO;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.SubscriberRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {
    private SubscriberRepo subscriberRepo;
    private ModelMapper modelMapper;

    @Override
    public NewsSubscriberVO save(NewsSubscriberVO newsSubscriberVO) {
        log.info(LogMessage.IN_SAVE, newsSubscriberVO);
        NewsSubscriber newsSubscriber = modelMapper.map(newsSubscriberVO, NewsSubscriber.class);
        return modelMapper.map(subscriberRepo.save(newsSubscriber), NewsSubscriberVO.class);
    }

    @Override
    public NewsSubscriberVO findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);
        return modelMapper.map(
                subscriberRepo.findById(id)
                        .orElseThrow(
                                () -> new NotFoundException(ErrorMessage.SUBSCRIBER_NOT_FOUND_BY_ID)),
                NewsSubscriberVO.class
        );
    }

    @Override
    public List<NewsSubscriberVO> findAll() {
        log.info(LogMessage.IN_FIND_ALL);
        return modelMapper.map(subscriberRepo.findAll(), new TypeToken<List<NewsSubscriberVO>>() {}.getType());
    }

    @Override
    public Long deleteById(Long id) {
        modelMapper.map(subscriberRepo.findById(id), NewsSubscriber.class);
        return id;
    }

    @Override
    public NewsSubscriberVO findByEmail(String email) {
        return modelMapper.map(subscriberRepo.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorMessage.SUBSCRIBER_NOT_FOUND_BY_EMAIL)),
                NewsSubscriberVO.class
        );
    }

    @Override
    public Long unsubscribe(String email, String unsubscribeToken) {
        NewsSubscriber newsSubscriber = subscriberRepo.findByEmailAndUnsubscribeToken(email, unsubscribeToken)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.SUBSCRIBER_NOT_FOUND_BY_EMAIL_AND_TOKEN));
        System.out.println(newsSubscriber);
        return newsSubscriber.getId();
    }
}
