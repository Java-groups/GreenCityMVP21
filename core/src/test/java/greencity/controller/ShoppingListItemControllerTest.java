package greencity.controller;

import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.ShoppingListItemServiceImpl;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemControllerTest {
    private static final String ENDPOINT = "/user/shopping-list-items";
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final long DEFAULT_HABIT_ID = ModelUtils.getHabitAssign().getId();

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ShoppingListItemServiceImpl shoppingListItemService;
    @Mock
    private UserService userService;
    @Mock
    private UserVO userVO;
    @InjectMocks
    private ShoppingListItemController shoppingListItemController;
    @Mock
    private Validator mockValidator;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        userVO = ModelUtils.getUserVO();
        mockMvc = MockMvcBuilders
                .standaloneSetup(shoppingListItemController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper)
                ).setValidator(mockValidator)
                .build();
    }

    @Test
    void findAllByUserTest() throws Exception {
        List<ShoppingListItemDto> listItemDtos = List.of(
                ShoppingListItemDto.builder().id(1L).text("Item1").status("INPROGRESS").build(),
                ShoppingListItemDto.builder().id(2L).text("Item2").status("INPROGRESS").build()
        );

        when(shoppingListItemService.findInProgressByUserIdAndLanguageCode(anyLong(), anyString())).thenReturn(listItemDtos);

        mockMvc.perform(get(ENDPOINT + "/{userId}/get-all-inprogress", 1L)
                        .param("lang", "en")
                        .locale(DEFAULT_LOCALE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].text").value("Item1"))
                .andExpect(jsonPath("$[0].status").value("INPROGRESS"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].text").value("Item2"))
                .andExpect(jsonPath("$[1].status").value("INPROGRESS"));

        verify(shoppingListItemService, times(1)).findInProgressByUserIdAndLanguageCode(anyLong(), eq("en"));
    }

    @Test
    void deleteTest() throws Exception {
        long shoppingListItemId = ModelUtils.getUserShoppingListItem().getId();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doNothing().when(shoppingListItemService).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(
                eq(shoppingListItemId), eq(userVO.getId()), eq(DEFAULT_HABIT_ID)
        );

        mockMvc.perform(delete(ENDPOINT)
                        .principal(userVO::getEmail)
                        .param("habitId", String.valueOf(DEFAULT_HABIT_ID))
                        .param("shoppingListItemId", String.valueOf(shoppingListItemId)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(shoppingListItemService, atMost(1))
                .deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(
                        eq(shoppingListItemId), eq(userVO.getId()), eq(DEFAULT_HABIT_ID)
                );
    }

    @Test
    void saveUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        List<UserShoppingListItemResponseDto> listItemDtos = List.of(ModelUtils.getUserShoppingListItemResponseDto());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.saveUserShoppingListItems(eq(1L), anyLong(), anyList(), anyString())).thenReturn(listItemDtos);

        String json = """
                [
                  {
                    "id": 1
                  }
                ]
                """;

        mockMvc.perform(post(ENDPOINT)
                        .principal(userVO::getEmail)
                        .param("habitId", String.valueOf(DEFAULT_HABIT_ID))
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(shoppingListItemService, times(1)).saveUserShoppingListItems(eq(userVO.getId()),
                eq(DEFAULT_HABIT_ID),
                anyList(),
                eq(DEFAULT_LOCALE.getLanguage()));
    }

    @Test
    void getUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.getUserShoppingList(
                eq(userVO.getId()),
                eq(DEFAULT_HABIT_ID),
                eq(DEFAULT_LOCALE.getLanguage()))
        ).thenReturn(List.of(ModelUtils.getUserShoppingListItemResponseDto()));

        mockMvc.perform(get(ENDPOINT + "/habits/{DEFAULT_HABIT_ID}/shopping-list", DEFAULT_HABIT_ID)
                        .principal(userVO::getEmail))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateUserShoppingListItemStatusWithoutLanguageParamTest() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.updateUserShoppingListItemStatus(
                anyLong(),
                anyLong(),
                anyString(),
                anyString()
        )).thenReturn(List.of(
                UserShoppingListItemResponseDto.builder().id(1L).status(ShoppingListItemStatus.INPROGRESS).text("BOOK").build()
        ));

        String json = """
                [
                  {
                    "id": 5
                  }
                ]
                """;

        mockMvc.perform(patch(ENDPOINT + "/{userShoppingListItemId}/status/{status}", 1L, "DONE")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userShoppingListItemId", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(userVO::getEmail))
                .andExpect(status().isOk())
                .andDo(print());

        verify(shoppingListItemService, times(1)).updateUserShoppingListItemStatus(
                anyLong(),
                anyLong(),
                anyString(),
                anyString()
        );
    }
}
