package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.FriendshipRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.friend.FriendshipRequest;
import greencity.entity.friend.FriendshipRequestId;
import greencity.enums.FriendshipRequestStatus;
import greencity.exception.exceptions.InvalidDataException;
import greencity.mapping.FriendshipRequestMapper;
import greencity.repository.FriendshipRequestRepo;
import greencity.repository.UserRepo;
import greencity.utils.FriendUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendshipRequestServiceImpl implements FriendshipRequestService {
    private final FriendshipRequestRepo requestRepo;
    private final UserRepo userRepo;
    private final FriendshipService friendshipService;
    private final ModelMapper modelMapper;
    private final FriendUtils friendUtils;

    @Override
    public PageableDto<FriendshipRequestDto> getFriendshipRequests(Long userId, Pageable pageable) {
        Page<User> pages = userRepo.getFriendshipRequests(userId, FriendshipRequestStatus.PENDING.name(), pageable);
        List<FriendshipRequestDto> requestDtos = pages.getContent()
                .stream()
                .map(user -> modelMapper.map(user, FriendshipRequestDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(requestDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }

    @Override
    @Transactional
    public String updateFriendshipRequest(Long userId, Long friendId, FriendshipRequestStatus status, UserVO user) {
        friendUtils.validateAdminOrCurrentUser(userId, user);
        friendUtils.validateUsersForFriendship(userId, friendId);
        friendUtils.checkFriendshipExistsOrThrow(userId, friendId);

        Optional<FriendshipRequest> optionalRequest = requestRepo.findById(new FriendshipRequestId(userId, friendId));

        if (optionalRequest.isPresent()) {
            FriendshipRequest currentRequest = optionalRequest.get();
            FriendshipRequestStatus currentStatus = currentRequest.getStatus();

            switch (status) {
                case CANCELLED:
                    requestRepo.delete(currentRequest);
                    return String.format("Friend request by user ID %d has been cancelled ", friendId);
                case ACCEPTED:
                    if (currentStatus.equals(FriendshipRequestStatus.PENDING)) {
                        String messageCreatedFriends = friendshipService.addFriend(userId, friendId, user);
                        requestRepo.delete(currentRequest);
                        return messageCreatedFriends;
                    } else {
                        throw new InvalidDataException(String.format("Cannot accept friendship request. "
                                + "The request was previously rejected by friend by ID %d.", friendId));
                    }
                default:
                    requestRepo.save(FriendshipRequestMapper.toEntity(userId, friendId, status));
            }
        } else {
            if (status.equals(FriendshipRequestStatus.PENDING)) {
                requestRepo.save(FriendshipRequestMapper.toEntity(userId, friendId, status));
            } else {
                throw new InvalidDataException(String.format("Invalid status %s. "
                        + "PENDING status is expected between users %d and %d.", status.name(), userId, friendId));
            }
        }
        return String.format("Request with status %s successfully sent to friend by ID %d", status.name(), friendId);
    }
}
