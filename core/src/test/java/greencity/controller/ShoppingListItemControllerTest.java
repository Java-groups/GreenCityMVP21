package greencity.controller;


import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.*;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mockValidator)
                .build();
    }

    @Test
    void bulkDeleteUserShoppingListItemTest() throws Exception{
        // Verify the response to a request for bulk deletion of user shopping list items.
        //Expected: status().isOk()

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
    void updateUserShoppingListItemStatusWithLanguageParamTest() throws Exception{
        //Verify the response to updating a shopping list item status with a specified language parameter.
        //Expected: status().isCreated()

        Locale locale = Locale.FRENCH;
        Long userShoppingListItemId = 211L;
        Long userId = 128L;

        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(userId).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.updateUserShopingListItemStatus(userId, userShoppingListItemId, locale.getLanguage()))
                .thenReturn(userShoppingListItemResponseDto);


        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}", userShoppingListItemId)
                        .header("Accept-Language", locale.getLanguage())
                        .param("id", userId.toString()))
                        .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(userId, userShoppingListItemId, locale.getLanguage());
    }


    @Test
    void updateUserShoppingListItemStatus() throws Exception {
//        Verify the response to updating a shopping list item's status.
//        Expected: status().isOk()

        Locale locale = Locale.FRENCH;
        Long userShoppingListItemId = 211L;
        Long userId = 128L;
        String status = ShoppingListItemStatus.INPROGRESS.name();

        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(userId).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.updateUserShoppingListItemStatus(userId, userShoppingListItemId, locale.getLanguage(), status))
                .thenReturn(List.of(userShoppingListItemResponseDto));


        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}/status/{status}", userShoppingListItemId, status)
                        .header("Accept-Language", locale.getLanguage())
                        .param("id", userId.toString()))
                .andExpect(status().isOk());

        verify(shoppingListItemService).updateUserShoppingListItemStatus(userId, userShoppingListItemId, locale.getLanguage(), status);
    }


    @Test
    void updateUserShoppingListItemStatusWithoutLanguageParamTest() throws Exception {
//        Verify the response to updating a shopping list item status without specifying a language parameter.
//        Expected: status().isCreated()

        String localeDefault = Locale.ENGLISH.getLanguage();
        Long userShoppingListItemId = 211L;
        Long userId = 128L;

        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(userId).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.updateUserShopingListItemStatus(userId, userShoppingListItemId, localeDefault))
                .thenReturn(userShoppingListItemResponseDto);


        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}", userShoppingListItemId)
                        .param("id", userId.toString()))
                .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(userId, userShoppingListItemId, localeDefault);
    }

    @Test
    void saveUserShoppingListItemWithoutLanguageParamTest() throws Exception {
//        Test saving a user's shopping list item without specifying a language parameter.
//        Expected: status().isCreated()

        String localeDefault = Locale.ENGLISH.getLanguage();
        Long habitId = 45L;
        Long userId = 128L;

        ShoppingListItemRequestDto shoppingListItemRequestDto = ShoppingListItemRequestDto.builder().id(1L).build();
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto.builder().build();

        when(shoppingListItemService.saveUserShoppingListItems(userId, habitId, List.of(shoppingListItemRequestDto), localeDefault))
                .thenReturn(List.of(userShoppingListItemResponseDto));


        String requestBody = "[{\"id\":1}]";


        mockMvc.perform(post(shoppingListItemLink)
                        .header("Accept-Language", localeDefault)
                        .param("id", userId.toString())
                        .param("habitId", habitId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated());

        verify(shoppingListItemService).saveUserShoppingListItems(userId, habitId, List.of(shoppingListItemRequestDto), localeDefault);
    }

    @Test
    void getUserShoppingListItemsWithLanguageParamTest() throws Exception{
//        Verify the response to a request for user shopping list items with a specified language parameter.
//        Expected: status().isOk()

        Locale locale = Locale.FRENCH;
        Long habitId = 45L;
        Long userId = 128L;

        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(userId).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.getUserShoppingList(userId, habitId, locale.getLanguage()))
                .thenReturn(List.of(userShoppingListItemResponseDto));


        mockMvc.perform(get(shoppingListItemLink + "/habits/{habitId}/shopping-list", habitId)
                        .header("Accept-Language", locale.getLanguage())
                        .param("id", userId.toString()))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(userId, habitId, locale.getLanguage());
    }

    @Test
    void getUserShoppingListItemWithoutLanguageParamTest() throws Exception{
//        Verify the response to a request for user shopping list items without specifying a language parameter.
//        Expected: status().isOk()

        String localeDefault = Locale.ENGLISH.getLanguage();
        Long habitId = 45L;
        Long userId = 128L;

        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto
                .builder().id(userId).text("Created").status(ShoppingListItemStatus.INPROGRESS).build();

        when(shoppingListItemService.getUserShoppingList(userId, habitId, localeDefault))
                .thenReturn(List.of(userShoppingListItemResponseDto));


        mockMvc.perform(get(shoppingListItemLink + "/habits/{habitId}/shopping-list", habitId)
                        .param("id", userId.toString()))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(userId, habitId, localeDefault);
    }

    @Test
    void deleteTest() throws Exception{
        //Test deleting a user's shopping list item by item ID, user ID, and habit ID.
        //Expected: status().isOk()

        mockMvc.perform(delete(shoppingListItemLink + "?id=128")
                        .param("shoppingListItemId", "4")
                        .param("habitId", "23"))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(4L, 128L, 23L);
    }


    @Test
    void findAllByUserTest() throws Exception{
//        Verify the response to a request for all items in progress for a specific user.
//        Expected: status().isOk()

        Long userId = 128L;
        String locale = Locale.GERMAN.toString();

        ShoppingListItemDto shoppingListItemDto = ShoppingListItemDto.builder().id(userId).text(locale).build();

        when(shoppingListItemService.findInProgressByUserIdAndLanguageCode(userId, locale))
                .thenReturn(List.of(shoppingListItemDto));

        mockMvc.perform(get(shoppingListItemLink + "/{userId}/get-all-inprogress", userId.toString())
                        .param("lang", locale))
                        .andExpect(status().isOk());

        verify(shoppingListItemService).findInProgressByUserIdAndLanguageCode(userId, locale);
    }
}