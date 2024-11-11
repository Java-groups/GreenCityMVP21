package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.subscriber.NewsSubscriberDto;
import greencity.dto.subscriber.NewsSubscriberVO;
import greencity.service.EmailService;
import greencity.service.SubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/newsSubscriber")
@AllArgsConstructor
@Slf4j
public class NewsSubscriberController {
    private SubscriberService subscriberService;
    private EmailService emailService;

    @Operation(summary = "Subscribing user to newsletter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping
    public ResponseEntity<NewsSubscriberDto> addSubscriber(@Valid @RequestBody NewsSubscriberDto newsSubscriberDto) {
        NewsSubscriberVO newsSubscriberVO = NewsSubscriberDto.toNewsSubscriberVO(newsSubscriberDto);
        subscriberService.save(NewsSubscriberDto.toNewsSubscriberVO(newsSubscriberDto));
        log.info("News Subscriber added successfully.");
        emailService.sendConfirmationLetter(newsSubscriberVO);
        log.info("Confirmation letter sent successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(newsSubscriberDto);
    }

    @Operation(summary = "Unsubscribing user from newsletter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/unsubscribe")
    public ResponseEntity<Long> unsubscribe(@Valid @RequestParam("email") String email,
                                            @RequestParam("unsubscribeToken") String unsubscribeToken) {
        long unsubscribeId = subscriberService.unsubscribe(email, unsubscribeToken);
        log.info("Successfully unsubscribed with id: {} user from newsletter.", unsubscribeId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Sends eco news to all subscribers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/sendNews")
    public ResponseEntity<List<NewsSubscriberVO>> sendNews(@RequestBody AddEcoNewsDtoResponse addEcoNewsDtoResponse) {
        List<NewsSubscriberVO> subscribers = subscriberService.findAll();
        log.info("News subscribers found successfully.");
        emailService.sendNewNewsForSubscriber(subscribers, addEcoNewsDtoResponse);
        log.info("Newsletters sent successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(subscribers);
    }
}
