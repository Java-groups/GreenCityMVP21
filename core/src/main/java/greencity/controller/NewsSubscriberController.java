package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.subscriber.NewsSubscriberDto;
import greencity.dto.subscriber.NewsSubscriberVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.service.EmailService;
import greencity.service.SubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/newsSubscriber")
@Slf4j
public class NewsSubscriberController {
    private SubscriberService subscriberService;
    private EmailService emailService;
    private final String REDIRECT_URL;

    public NewsSubscriberController(SubscriberService subscriberService,
                                    EmailService emailService,
                                    @Value("${econews.address}") String REDIRECT_URL) {
        this.subscriberService = subscriberService;
        this.emailService = emailService;
        this.REDIRECT_URL = REDIRECT_URL;
    }


    @Operation(summary = "Subscribing user to newsletter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT)
    })
    @PostMapping
    public ResponseEntity<NewsSubscriberDto> addSubscriber(@Valid @RequestBody NewsSubscriberDto newsSubscriberDto) {
        NewsSubscriberVO newsSubscriberVO = NewsSubscriberDto.toNewsSubscriberVO(newsSubscriberDto);
        HttpStatus status = HttpStatus.CREATED;
        try {
            subscriberService.save(newsSubscriberVO);
            log.info("News Subscriber added successfully.");
            emailService.sendConfirmationLetter(newsSubscriberVO);
            log.info("Confirmation letter sent successfully.");
        } catch (RuntimeException e) {
            status = HttpStatus.CONFLICT;
        }
        return ResponseEntity.status(status).body(newsSubscriberDto);
    }

    @Operation(summary = "Unsubscribing user from newsletter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@Valid @RequestParam("email") String email,
                                            @RequestParam("unsubscribeToken") String unsubscribeToken) {
        HttpStatus status = HttpStatus.OK;
        try {
            long unsubscribeId = subscriberService.unsubscribe(email, unsubscribeToken);
            log.info("Successfully unsubscribed with id: {} user from newsletter.", unsubscribeId);
        } catch (NotFoundException e) {
            log.error("User with email {} not found.", email);
            status = HttpStatus.NOT_FOUND;
        }
        System.out.println(REDIRECT_URL);
        return ResponseEntity.status(status).location(URI.create(REDIRECT_URL)).build();
    }

    @Operation(summary = "Sends eco news to all subscribers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/sendNews")
    public ResponseEntity<List<NewsSubscriberVO>> sendNews(@RequestBody AddEcoNewsDtoResponse addEcoNewsDtoResponse) {
        List<NewsSubscriberVO> subscribers = subscriberService.findAll();
        if (!subscribers.isEmpty()) {
            log.info("News subscribers found successfully.");
            emailService.sendNewNewsForSubscriber(subscribers, addEcoNewsDtoResponse);
            log.info("Newsletters sent successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(subscribers);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
