package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static greencity.ModelUtils.getUserVO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FriendsServiceImplTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FriendsServiceImpl friendsService;

    private final User friend1 = User.builder()
        .id(1L)
        .email("david@gmail.com")
        .name("David")
        .role(Role.ROLE_USER)
        .userStatus(UserStatus.ACTIVATED)
        .lastActivityTime(LocalDateTime.now())
        .verifyEmail(new VerifyEmail())
        .dateOfRegistration(LocalDateTime.now())
        .build();

    private final UserFriendDto userFriend1Dto = UserFriendDto.builder()
        .id(2L)
        .email("david@gmail.com")
        .name("David")
        .city("Some")
        .rating(3.2)
        .mutualFriends(1L)
        .build();

    private final UserManagementDto userFriendManagementDto = UserManagementDto.builder()
        .id(1L)
        .email("emily@gmail.com")
        .name("Emily")
        .build();

    UserVO userVO = getUserVO();
    List<User> friends = List.of(friend1);
    Page<User> friendsPage = new PageImpl<>(friends);

    @Test
    void findFriendsByNameTest() {
        String name = "david";
        Pageable page = PageRequest.of(1, 10);


        when(userRepo.getAllUserFriends(name, userVO.getId(), page)).thenReturn(friendsPage);
        when(userRepo.getMutualFriendsCount(any(), any())).thenReturn(1L);
        when(modelMapper.map(any(), any())).thenReturn(userFriend1Dto);
        PageableAdvancedDto<UserFriendDto> actual = friendsService.findFriends(name, userVO.getId(), page);

        assertEquals(actual.getTotalElements(), 1L);
        assertEquals(actual.getPage().getFirst(), userFriend1Dto);
        verify(userRepo).getAllUserFriends(name, userVO.getId(), page);
        verify(userRepo).getMutualFriendsCount(any(), any());
        verify(modelMapper).map(friend1, UserFriendDto.class);
    }

    @Test
    void findFriendsByUdTest() {
        when(userRepo.getAllUserFriends(userVO.getId())).thenReturn(friends);
        when(modelMapper.map(any(), any())).thenReturn(userFriendManagementDto);
        List<UserManagementDto> actual = friendsService.findFriends(userVO.getId());

        assertEquals(actual, List.of(userFriendManagementDto));
        verify(userRepo).getAllUserFriends(userVO.getId());
        verify(modelMapper).map(friend1, UserManagementDto.class);
    }

    @Test
    void findFriendRequestsTest() {
        Pageable page = PageRequest.of(1, 10);
        when(userRepo.getAllUserFriendRequests(any(), any())).thenReturn(friendsPage);
        when(modelMapper.map(any(), any())).thenReturn(userFriend1Dto);
        PageableAdvancedDto<UserFriendDto> actual = friendsService.findFriendRequests(userVO.getId(), page);

        assertEquals(actual.getTotalElements(), 1);
        assertEquals(actual.getPage().getFirst(), userFriend1Dto);
        verify(userRepo).getAllUserFriendRequests(userVO.getId(), page);
        verify(modelMapper).map(friend1, UserFriendDto.class);
    }

    @Test
    void findNotFriendsYetTest() {
        String name = "david";
        Pageable page = PageRequest.of(1, 10);
        when(userRepo.getAllUserNotFriendsYet(any(), any(), any())).thenReturn(friendsPage);
        when(modelMapper.map(any(), any())).thenReturn(userFriend1Dto);
        PageableAdvancedDto<UserFriendDto> actual = friendsService.findNotFriendsYet(name, userVO.getId(), page);

        assertEquals(actual.getTotalElements(), 1);
        assertEquals(actual.getPage().getFirst(), userFriend1Dto);
        verify(userRepo).getAllUserNotFriendsYet(name, userVO.getId(), page);
        verify(modelMapper).map(friend1, UserFriendDto.class);
    }

    @Test
    void acceptFriendRequestFailureNoUserTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        assertThrows(NotFoundException.class, () -> friendsService.acceptFriendRequest(1L, 2L));
    }


    @Test
    void acceptFriendRequestFailureNoFriendTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> friendsService.acceptFriendRequest(1L, 2L));
    }

    @Test
    void acceptFriendRequestFailureNoRequestTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        assertThrows(NotFoundException.class, () -> friendsService.acceptFriendRequest(1L, 2L));
    }

    @Test
    void acceptFriendRequestSuccessTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        when(userRepo.isFriendRequestSent(2L, 1L)).thenReturn(true);
        when(userRepo.isFriendRequestSent(1L, 2L)).thenReturn(false);
        friendsService.acceptFriendRequest(1L, 2L);

        verify(userRepo).findById(1L);
        verify(userRepo).findById(2L);
        verify(userRepo).isFriendRequestSent(2L, 1L);
        verify(userRepo).deleteFriendRequest(2L, 1L);
        verify(userRepo).isFriendRequestSent(1L, 2L);
        verify(userRepo).addFriend(1L, 2L);
    }

    @Test
    void sendFriendRequestFailureNoUserTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        assertThrows(NotFoundException.class, () -> friendsService.sendFriendRequest(1L, 2L));
    }

    @Test
    void sendFriendRequestFailureNoFriendTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> friendsService.sendFriendRequest(1L, 2L));
    }

    @Test
    void sendFriendRequestFailureAlreadySentTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        when(userRepo.isFriendRequestSent(1L, 2L)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> friendsService.sendFriendRequest(1L, 2L));
    }

    @Test
    void sendFriendRequestSuccessTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        when(userRepo.isFriendRequestSent(1L, 2L)).thenReturn(false);
        friendsService.sendFriendRequest(1L, 2L);

        verify(userRepo).findById(1L);
        verify(userRepo).findById(2L);
        verify(userRepo).isFriendRequestSent(1L, 2L);
        verify(userRepo).saveFriendRequest(1L, 2L);
    }

    @Test
    void declineFriendRequestFailureNoUserTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        assertThrows(NotFoundException.class, () -> friendsService.declineFriendRequest(1L, 2L));
    }

    @Test
    void declineFriendRequestFailureNoUFriendTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> friendsService.declineFriendRequest(1L, 2L));
    }

    @Test
    void declineFriendRequestFailureNoSentTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        when(userRepo.isFriendRequestSent(2L, 1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> friendsService.declineFriendRequest(1L, 2L));
    }

    @Test
    void declineFriendRequestSuccessTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(friend1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(friend1));
        when(userRepo.isFriendRequestSent(2L, 1L)).thenReturn(true);
        friendsService.declineFriendRequest(1L, 2L);

        verify(userRepo).findById(1L);
        verify(userRepo).findById(2L);
        verify(userRepo).isFriendRequestSent(2L, 1L);
        verify(userRepo).deleteFriendRequest(2L, 1L);
    }
}
