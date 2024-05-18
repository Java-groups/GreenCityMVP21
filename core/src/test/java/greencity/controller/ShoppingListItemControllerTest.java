package greencity.controller;


import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemControllerTest {
    MockMvc mockMvc;

    @Mock
    ShoppingListItemService shoppingListItemService;

    @Mock
    Validator mockValidator;

    @InjectMocks
    ShoppingListItemController shoppingListItemController;

    static final String shoppingListItemLink = "/user/shopping-list-items";

    static final Long USER_ID = 128L;
    static final Long USER_SHOPPING_LIST_ITEM_ID = 211L;
    static final Long HABIT_ID = 45L;
    static final String LOCALE = Locale.FRENCH.getLanguage();
    static final String LOCALE_DEFAULT = Locale.ENGLISH.getLanguage();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mockValidator)
                .build();
    }

    @Test
    @DisplayName("Verify the response to a request for bulk deletion of user shopping list items. [Expected: status().isOk()]")
    void bulkDeleteUserShoppingListItemTest() throws Exception{
        List<Long> deletedItemIds = Arrays.asList(1L, 2L);
        when(shoppingListItemService.deleteUserShoppingListItems("1,2")).thenReturn(deletedItemIds);


        mockMvc.perform(delete(shoppingListItemLink + "/user-shopping-list-items")
                        .param("ids", "1,2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0]").value(1))
                        .andExpect(jsonPath("$[1]").value(2));

        verify(shoppingListItemService).deleteUserShoppingListItems("1,2");
    }

    @Test
    @DisplayName("Verify the response to updating a shopping list item status with a specified language parameter. [Expected: status().isCreated()]")
    void updateUserShoppingListItemStatusWithLanguageParamTest() throws Exception{
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(USER_ID).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.updateUserShopingListItemStatus(USER_ID, USER_SHOPPING_LIST_ITEM_ID, LOCALE))
                .thenReturn(userShoppingListItemResponseDto);

        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}", USER_SHOPPING_LIST_ITEM_ID)
                        .header("Accept-Language", LOCALE)
                        .param("id", USER_ID.toString()))
                        .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(USER_ID, USER_SHOPPING_LIST_ITEM_ID, LOCALE);
    }

    @Test
    @DisplayName("Verify the response to updating a shopping list item's status. [Expected: status().isOk()]")
    void updateUserShoppingListItemStatus() throws Exception {
        String status = ShoppingListItemStatus.INPROGRESS.name();

        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(USER_ID).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.updateUserShoppingListItemStatus(USER_ID, USER_SHOPPING_LIST_ITEM_ID, LOCALE, status))
                .thenReturn(List.of(userShoppingListItemResponseDto));

        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}/status/{status}", USER_SHOPPING_LIST_ITEM_ID, status)
                        .header("Accept-Language", LOCALE)
                        .param("id", USER_ID.toString()))
                .andExpect(status().isOk());

        verify(shoppingListItemService).updateUserShoppingListItemStatus(USER_ID, USER_SHOPPING_LIST_ITEM_ID, LOCALE, status);
    }


    @Test
    @DisplayName("Verify the response to updating a shopping list item status without specifying a language parameter. [Expected: status().isCreated()]")
    void updateUserShoppingListItemStatusWithoutLanguageParamTest() throws Exception {
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(USER_ID).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.updateUserShopingListItemStatus(USER_ID, USER_SHOPPING_LIST_ITEM_ID, LOCALE_DEFAULT))
                .thenReturn(userShoppingListItemResponseDto);

        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}", USER_SHOPPING_LIST_ITEM_ID)
                        .param("id", USER_ID.toString()))
                .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(USER_ID, USER_SHOPPING_LIST_ITEM_ID, LOCALE_DEFAULT);
    }

    @Test
    @DisplayName("Test saving a user's shopping list item without specifying a language parameter. [Expected: status().isCreated()]")
    void saveUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        ShoppingListItemRequestDto shoppingListItemRequestDto = ShoppingListItemRequestDto.builder().id(1L).build();
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto.builder().build();

        when(shoppingListItemService.saveUserShoppingListItems(USER_ID, HABIT_ID, List.of(shoppingListItemRequestDto), LOCALE_DEFAULT))
                .thenReturn(List.of(userShoppingListItemResponseDto));


        String requestBody = "[{\"id\":1}]";


        mockMvc.perform(post(shoppingListItemLink)
                        .header("Accept-Language", LOCALE_DEFAULT)
                        .param("id", USER_ID.toString())
                        .param("habitId", HABIT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated());

        verify(shoppingListItemService).saveUserShoppingListItems(USER_ID, HABIT_ID, List.of(shoppingListItemRequestDto), LOCALE_DEFAULT);
    }

    @Test
    @DisplayName("Verify the response to a request for user shopping list items with a specified language parameter. [Expected: status().isOk()]")
    void getUserShoppingListItemsWithLanguageParamTest() throws Exception{
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(USER_ID).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.getUserShoppingList(USER_ID, HABIT_ID, LOCALE))
                .thenReturn(List.of(userShoppingListItemResponseDto));

        mockMvc.perform(get(shoppingListItemLink + "/habits/{habitId}/shopping-list", HABIT_ID)
                        .header("Accept-Language", LOCALE)
                        .param("id", USER_ID.toString()))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(USER_ID, HABIT_ID, LOCALE);
    }

    @Test
    @DisplayName("Verify the response to a request for user shopping list items without specifying a language parameter. [Expected: status().isOk()]")
    void getUserShoppingListItemWithoutLanguageParamTest() throws Exception{
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(USER_ID).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.getUserShoppingList(USER_ID, HABIT_ID, LOCALE_DEFAULT))
                .thenReturn(List.of(userShoppingListItemResponseDto));

        mockMvc.perform(get(shoppingListItemLink + "/habits/{habitId}/shopping-list", HABIT_ID)
                        .param("id", USER_ID.toString()))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(USER_ID, HABIT_ID, LOCALE_DEFAULT);
    }

    @Test
    @DisplayName("Test deleting a user's shopping list item by item ID, user ID, and habit ID. [Expected: status().isOk()]")
    void deleteTest() throws Exception{
        mockMvc.perform(delete(shoppingListItemLink)
                        .param("id", USER_ID.toString())
                        .param("shoppingListItemId", USER_SHOPPING_LIST_ITEM_ID.toString())
                        .param("habitId", HABIT_ID.toString()))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(USER_SHOPPING_LIST_ITEM_ID, USER_ID, HABIT_ID);
    }

    @Test
    @DisplayName("Verify the response to a request for all items in progress for a specific user. [Expected: status().isOk()]")
    void findAllByUserTest() throws Exception{
        ShoppingListItemDto shoppingListItemDto = ShoppingListItemDto.builder().id(USER_ID).text(LOCALE).build();

        when(shoppingListItemService.findInProgressByUserIdAndLanguageCode(USER_ID, LOCALE))
                .thenReturn(List.of(shoppingListItemDto));

        mockMvc.perform(get(shoppingListItemLink + "/{userId}/get-all-inprogress", USER_ID.toString())
                        .param("lang", LOCALE))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).findInProgressByUserIdAndLanguageCode(USER_ID, LOCALE);
    }
}