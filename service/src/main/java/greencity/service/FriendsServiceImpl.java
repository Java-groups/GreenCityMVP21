package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public PageableAdvancedDto<UserFriendDto> findFriends(String name, Long userId, Pageable page) {
        return getPageableAdvancedDtoOfUserFriendDto(userId, userRepo.getAllUserFriends(name, userId, page));
    }

    @Override
    public List<UserManagementDto> findFriends(Long userId) {
        return userRepo.getAllUserFriends(userId).stream()
                .map(item -> modelMapper.map(item, UserManagementDto.class))
                .toList();
    }

    @Override
    public PageableAdvancedDto<UserFriendDto> findFriendRequests(Long userId, Pageable page) {
        return getPageableAdvancedDtoOfUserFriendDto(userId, userRepo.getAllUserFriendRequests(userId, page));
    }

    @Override
    public PageableAdvancedDto<UserFriendDto> findNotFriendsYet(String name, Long userId, Pageable page) {
        return getPageableAdvancedDtoOfUserFriendDto(userId, userRepo.getAllUserNotFriendsYet(name, userId, page));
    }

    @Transactional
    @Override
    public void acceptFriendRequest(Long userId, Long friendId) {
        userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        User friend = userRepo.findById(friendId).orElseThrow(() -> new NotFoundException("Friend not found!"));
        if (userRepo.getAllUserFriends(userId).contains(friend)) {
            throw new BadRequestException("Friend already accepted!");
        }
        if (userRepo.deleteFriendRequest(userId, friendId) == 0) {
            throw new NotFoundException("Friend request not found!");
        }
        userRepo.addFriend(userId, friendId);
    }

    @Transactional
    @Override
    public void sendFriendRequest(Long userId, Long friendId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        User friend = userRepo.findById(friendId).orElseThrow(() -> new NotFoundException("Friend not found!"));
        if (userRepo.getAllUserFriends(userId).contains(friend)) {
            throw new BadRequestException("User is already your friend!");
        }
        if (userRepo.countFriendRequest(userId, friendId) > 0) {
            throw new BadRequestException("Friend request already sent!");
        }
        userRepo.addFriendRequest(userId, friendId);
}


    @Transactional
    @Override
    public void cancelFriendRequest(Long userId, Long friendId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        User friend = userRepo.findById(friendId).orElseThrow(() -> new NotFoundException("Friend not found!"));
        if (userRepo.deleteFriendRequest(userId, friendId) == 0) {
            throw new NotFoundException("Friend request not found!");
        }
    }

    @Transactional
    @Override
    public void declineFriendRequest(Long userId, Long friendId) {
        cancelFriendRequest(userId, friendId);
    }

    private PageableAdvancedDto<UserFriendDto> getPageableAdvancedDtoOfUserFriendDto(
            Long userId, Page<User> friendsPage) {
        List<UserFriendDto> friendsList = friendsPage.stream()
                .map(user -> modelMapper.map(user, UserFriendDto.class))
                .peek(userFriendDto -> userFriendDto.setMutualFriends(
                        userRepo.getMutualFriendsCount(userId, userFriendDto.getId())))
                .toList();
        return new PageableAdvancedDto<>(
                friendsList,
                friendsPage.getTotalElements(),
                friendsPage.getNumber(),
                friendsPage.getTotalPages(),
                friendsPage.getNumber(),
                friendsPage.hasPrevious(),
                friendsPage.hasNext(),
                friendsPage.isFirst(),
                friendsPage.isLast()
        );
    }
}
