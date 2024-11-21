package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/events/comments")
public class EventCommentController {
    private EventCommentService eventCommentService;

    @Operation(summary = "Create comment to an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddEventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{eventId}")
    public ResponseEntity<AddEventCommentDtoResponse> addComment(
            @PathVariable("eventId") long eventId,
            @RequestBody @Valid AddEventCommentDtoRequest addEventCommentDtoRequest,
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventCommentService.save(eventId, addEventCommentDtoRequest, currentUser));
    }
    @Operation(summary = "Update comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping
    public ResponseEntity<?> update(Long id, @RequestParam @NotBlank String text,
                       @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.update(text, id, user);
        return ResponseEntity.ok().build();
    }
}
