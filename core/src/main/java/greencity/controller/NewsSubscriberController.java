package greencity.controller;

import greencity.dto.subscriber.NewsSubscriberDto;
import greencity.service.SubscriberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/newsSubscriber")
@AllArgsConstructor
@Slf4j
public class NewsSubscriberController {
    private SubscriberService subscriberService;

    @PostMapping
    public ResponseEntity<NewsSubscriberDto> addSubscriber(@Valid @RequestBody NewsSubscriberDto newsSubscriberDto) {
        subscriberService.save(NewsSubscriberDto.toNewsSubscriberVO(newsSubscriberDto));
        log.info("News Subscriber added successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(newsSubscriberDto);
    }
}
