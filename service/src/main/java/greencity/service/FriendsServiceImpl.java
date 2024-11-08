package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import greencity.entity.User;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public PageableAdvancedDto<UserFriendDto> findFriends(String name, Long userId, Pageable page) {
        Page<User> friendsPage = userRepo.getAllUserFriends(name, userId, page);
        List<UserFriendDto> friendsList = friendsPage.stream()
                .map(user -> modelMapper.map(user, UserFriendDto.class))
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
