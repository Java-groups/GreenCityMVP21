package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.annotations.ImageArrayValidation;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.EventAttendanceDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventRequestSaveDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.event.EventUpdateRequestDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {

    private final EventService eventService;

    /**
     * Method for creating {@link Event}
     *
     * @param eventRequestSaveDto - dto for {@link EventRequestSaveDto} entity.
     * @param images - array of {@link MultipartFile} images.
     * @param user - current user {@link UserVO}.
     * @return dto {@link EventResponseDto} instance.
     */
    @Operation(summary = "Create Event.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "415", description = HttpStatuses.UNSUPPORTED_MEDIA_TYPE)
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventResponseDto> save(
            @Parameter(description = SwaggerExampleModel.ADD_EVENT, required = true)
            @RequestPart @Valid EventRequestSaveDto eventRequestSaveDto,
            @Parameter(description = "Upload array of images for event.")
            @ImageArrayValidation @Size(max = 5, message = "Download up to 5 images") MultipartFile[] images,
            @Parameter(hidden = true) @CurrentUser UserVO user
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                eventService.save(eventRequestSaveDto, images, user));
    }

    /**
     * Method for getting all events by page.
     *
     * @return PageableDto of {@link EventResponseDto} instances.
     */
    @Operation(summary = "Find all events by page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping()
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<EventResponseDto>> findAll(@Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAll(page));
    }

    /**
     * Method for getting event by id.
     *
     * @return {@link EventResponseDto} instance.
     */
    @Operation(summary = "Get event by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.findById(id));
    }

    /**
     * Method for getting eco news by authorised user.
     *
     * @return list of {@link EventResponseDto} instances.
     */
    @Operation(summary = "Get events by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @ApiPageable
    @GetMapping("/author/{userId}")
    public ResponseEntity<PageableAdvancedDto<EventResponseDto>> getEventByAuthorId(@PathVariable Long userId,
                                                                                    @Parameter(hidden = true) Pageable page ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.findAllByAuthor(page, userId));
    }

    /**
     * Updates an existing event with new data provided via DTO.
     * This method allows updating the event if the principal (current user) has appropriate permissions.
     *
     * @param eventDto the DTO containing updated data for the event.
     * @param principal the security principal representing the currently authenticated user.
     * @return a {@link ResponseEntity} with {@link EventResponseDto}, reflecting the updated event data.
     */
    @Operation(summary = "Update an existing event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the event",
                    content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, possibly due to validation issues"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request, authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden, current user does not have permission to update this event"),
            @ApiResponse(responseCode = "404", description = "The event with specified ID was not found")
    })
    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventResponseDto> update(
            @Parameter(description = SwaggerExampleModel.UPDATE_EVENT, required = true)
            @RequestPart("event") @Valid EventUpdateRequestDto eventDto,
            @RequestPart(value = "images", required = false)
            @ImageArrayValidation @Size(max = 5, message = "Download up to 5 images") MultipartFile[] images,
            @Parameter(hidden = true) Principal principal) {

        return ResponseEntity.ok().body(eventService.update(eventDto, images, principal.getName()));
    }

    /**
     * Method for deleting an event by a given Event ID.
     *
     * @param eventId the ID of the event to be deleted.
     * @param principal is automatically inserted via SecurityContextHolder, in this context - the user's name.
     * @return ResponseEntity<Object> this returns server's response which denotes the status of the operation.
     */
    @Operation(summary = "Delete event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId, @Parameter(hidden = true) Principal principal) {
        eventService.delete(eventId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
  
    /**
     * Method for adding an attender to the event.
     *
     * @author Roman Kasarab
     */
    @Operation(summary = "Add an attender to the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/attender/{eventId}")
    public void addAttender(@PathVariable Long eventId, @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventService.addAttender(eventId, user);
    }

    /**
     * Method for getting all attenders of the event.
     *
     * @author Roman Kasarab
     */
    @Operation(summary = "Find all attenders of the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/attender/{eventId}")
    public ResponseEntity<Set<EventAttendanceDto>> getAttenders(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAllAttendersByEvent(eventId));
    }
  
    /**
     * Method for removing an attender from the event.
     *
     * @author Roman Kasarab
     */
    @Operation(summary = "Remove an attender from the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/attender/{eventId}")
    public ResponseEntity<Object> deleteAttender(@PathVariable Long eventId, @Parameter(hidden = true) @CurrentUser Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.removeAttender(eventId, principal.getName()));
    }
}
