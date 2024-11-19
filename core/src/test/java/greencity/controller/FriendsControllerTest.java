package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserVO;
import greencity.service.FriendsService;
import greencity.service.UserService;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FriendsControllerTest {
    private MockMvc mockMvc;

    @Mock
    private FriendsService friendsService;

    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FriendsController friendsController;

    private final String friendPrefixLink = "/friends";

    private Principal principal = getPrincipal();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(friendsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    void findAllFriendsOfUserTest() throws Exception {
        String name = "david";
        Pageable pageable = PageRequest.of(1, 10);
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        String url =
            String.format("%s?name=%s&page=%d&size=%d", friendPrefixLink, name, pageable.getPageNumber(),
                pageable.getPageSize());

        mockMvc
            .perform(get(url).principal(principal))
            .andExpect(status().isOk());

        verify(friendsService).findFriends(name, userVO.getId(), pageable);
    }

    @Test
    void findAllFriendRequestsOfUserTest() throws Exception {
        Pageable pageable = PageRequest.of(1, 10);
        String url =
            String.format("%s/friendRequests?page=%d&size=%d", friendPrefixLink, pageable.getPageNumber(),
                pageable.getPageSize());
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        mockMvc
            .perform(get(url).principal(principal))
            .andExpect(status().isOk());

        verify(friendsService).findFriendRequests(userVO.getId(), pageable);
    }

    @Test
    void findAllNotFriendsYetOfUserTest() throws Exception {
        String name = "david";
        Pageable pageable = PageRequest.of(1, 10);
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        String url =
            String.format("%s/not-friends-yet?name=%s&page=%d&size=%d", friendPrefixLink, name,
                pageable.getPageNumber(),
                pageable.getPageSize());

        mockMvc
            .perform(get(url).principal(principal))
            .andExpect(status().isOk());

        verify(friendsService).findNotFriendsYet(name, userVO.getId(), pageable);
    }

    @Test
    void acceptFriendRequestTest() throws Exception {
        Long friendId = 1L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc
            .perform(patch(String.format("%s/%d/acceptFriend", friendPrefixLink, friendId)).principal(principal))
            .andExpect(status().isOk());

        verify(friendsService).acceptFriendRequest(userVO.getId(), friendId);
    }

    @Test
    void findUserFriendsByUserIdTest() throws Exception {
        Long userId = 1L;
        mockMvc
            .perform(get(String.format("%s/user/%d", friendPrefixLink, userId)))
            .andExpect(status().isOk());

        verify(friendsService).findFriends(userId);
    }

    @Test
    void sendFriendRequestTest() throws Exception {
        Long friendId = 1L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        mockMvc
            .perform(post(String.format("%s/%d", friendPrefixLink, friendId)).principal(principal))
            .andExpect(status().isOk());

        verify(friendsService).sendFriendRequest(userVO.getId(), friendId);
    }

    @Test
    void declineFriendRequestTest() throws Exception {
        Long friendId = 1L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc
            .perform(delete(String.format("%s/%d/declineFriend", friendPrefixLink, friendId)).principal(principal))
            .andExpect(status().isOk());

        verify(friendsService).declineFriendRequest(userVO.getId(), friendId);
    }
}
