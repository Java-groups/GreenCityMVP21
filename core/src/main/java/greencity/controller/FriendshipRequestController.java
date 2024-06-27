package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.friend.FriendshipRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.FriendshipRequestStatus;
import greencity.service.FriendshipRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/friendshipRequest")
public class FriendshipRequestController {
    private final FriendshipRequestService friendshipRequestService;

    @Operation(summary = "Creates or upgrades statuses in friend requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @ApiLocale
    @PostMapping("/{userId}")
    public ResponseEntity<String> updateFriendRequest(
            @PathVariable Long userId,
            @RequestParam Long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO user,
            @Parameter(in = ParameterIn.QUERY, description = "Status of the friendship request")
            @RequestParam FriendshipRequestStatus status) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendshipRequestService.updateFriendshipRequest(userId, friendId, status, user));
    }

    @Operation(summary = "Receives all friend requests by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @ApiPageableWithLocale
    @GetMapping("/requests/{userId}")
    public ResponseEntity<PageableDto<FriendshipRequestDto>> getFriendshipRequests(
            @PathVariable Long userId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendshipRequestService.getFriendshipRequests(userId, pageable));
    }
}
