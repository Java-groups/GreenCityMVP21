package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendsController {
    FriendsService friendsService;

    @Operation(summary = "Get all friends of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("")
    public ResponseEntity<PageableAdvancedDto<UserFriendDto>> findAllFriendsOfUser(
            @RequestParam(required = false) String name,
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(friendsService.findFriends(name, currentUser.getId(), page));
    }

    @Operation(summary = "Get all friends requests of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("/friendRequests")
    public ResponseEntity<PageableAdvancedDto<UserFriendDto>> findAllFriendRequestsOfUser(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(friendsService.findFriendRequests(currentUser.getId(), page));
    }

    @Operation(summary = "Get all users except main user and users friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableAdvancedDto<UserFriendDto>> findAllNotFriendsYetOfUser(
            @RequestParam(required = false) String name,
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(friendsService.findNotFriendsYet(name, currentUser.getId(), page));
    }

    @Operation(summary = "Accept friend request to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/{friendId}/acceptFriend")
    public ResponseEntity<?>  acceptFriendRequest(
            @PathVariable(name = "friendId") Long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        friendsService.acceptFriendRequest(currentUser.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Get all user friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/friends/user/{userId}")
    public ResponseEntity<List<UserManagementDto>> findUserFriendsByUserId(
            @RequestParam() Long userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(friendsService.findFriends(userId));
    }

    @Operation(summary = "Send a friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<?> sendFriendRequest(
            @PathVariable(name = "friendId") Long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        friendsService.sendFriendRequest(currentUser.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Cancel a friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}/cancelRequest")
    public ResponseEntity<?> cancelFriendRequest(
            @PathVariable(name = "friendId") Long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        friendsService.cancelFriendRequest(currentUser.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Decline friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}/declineFriend")
    public ResponseEntity<?> declineFriendRequest(
            @PathVariable(name = "friendId") Long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        friendsService.declineFriendRequest(currentUser.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
