package greencity.controller;

import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CustomShoppingListItemControllerTest {
    static final String customShoppingListItemsLink = "/custom/shopping-list-items";
    static final Long USER_ID = 1L;
    static final Long HABIT_ID = 1L;
    static final Long ITEM_ID = 1L;
    static final String ITEM_STATUS = "DONE";
    MockMvc mockMvc;
    @Mock
    CustomShoppingListItemService customShoppingListItemService;
    @InjectMocks
    CustomShoppingListItemController customShoppingListItemController;
    CustomShoppingListItemResponseDto responseDto;
    final ObjectMapper objectMapper = new ObjectMapper();
    Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        responseDto = new CustomShoppingListItemResponseDto(3L, "test", ShoppingListItemStatus.ACTIVE);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(customShoppingListItemService);
    }

    @Test
    @DisplayName("Get all available custom shopping-list-items with authenticated user")
    void getAllAvailableCustomShoppingListItems_WithAuthenticatedUser_ReturnsOk() throws Exception {
        mockMvc.perform(get(customShoppingListItemsLink + "/{userId}/{habitId}", USER_ID, HABIT_ID)
                        .principal(principal))
                .andExpect(status().isOk());
        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(USER_ID, HABIT_ID))
                .thenReturn(List.of(responseDto));
        verify(customShoppingListItemService).findAllAvailableCustomShoppingListItems(USER_ID, HABIT_ID);
        assertEquals(responseDto, Objects.requireNonNull(customShoppingListItemController.getAllAvailableCustomShoppingListItems(USER_ID, HABIT_ID)
                .getBody()).getFirst());
    }

    @Test
    @DisplayName("Get all custom shopping items by status for a specific user")
    void getAllCustomShoppingItemsByStatus_ForSpecificUser_ReturnsOk() throws Exception {
        mockMvc.perform(get(customShoppingListItemsLink + "/{userId}/custom-shopping-list-items", USER_ID)
                        .principal(principal))
                .andExpect(status().isOk());
        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(anyLong(),anyString()))
                .thenReturn(List.of(responseDto));
        assertEquals(responseDto, customShoppingListItemController.getAllCustomShoppingItemsByStatus(USER_ID, "ACTIVE")
                .getBody().getFirst());
    }

    @Test
    @DisplayName("Save one or multiple custom shopping list items for a specific user")
    void saveCustomShoppingListItems_ForSpecificUser_ReturnsCreated() throws Exception {
        BulkSaveCustomShoppingListItemDto bulkSaveDto = new BulkSaveCustomShoppingListItemDto();
        String content = objectMapper.writeValueAsString(bulkSaveDto);
        mockMvc.perform(post(customShoppingListItemsLink + "/{userId}/{habitAssignId}/custom-shopping-list-items", USER_ID, HABIT_ID)
                        .principal(principal)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        when(customShoppingListItemService.save(bulkSaveDto, USER_ID, HABIT_ID))
                .thenReturn(List.of(responseDto));
        verify(customShoppingListItemService).save(bulkSaveDto, USER_ID, HABIT_ID);
        assertEquals(responseDto, customShoppingListItemController.saveUserCustomShoppingListItems(bulkSaveDto, USER_ID, HABIT_ID)
                .getBody().getFirst());
    }

    @Test
    @DisplayName("Update status of custom shopping list items for a specific user")
    void updateCustomShoppingListItemsStatus_ForSpecificUser_ReturnsOk() throws Exception {
        mockMvc.perform(patch(customShoppingListItemsLink + "/{userId}/custom-shopping-list-items/?itemId={itemId}&status={status}", USER_ID, ITEM_ID, ITEM_STATUS)
                        .principal(principal))
                .andExpect(status().isOk());
        verify(customShoppingListItemService).updateItemStatus(USER_ID, ITEM_ID, ITEM_STATUS);
    }

    @Test
    @DisplayName("Update status of custom shopping list items for a specific user")
    void updateItemStatusToDone_ForSpecificUser_ReturnsOk() throws Exception {
        mockMvc.perform(patch(customShoppingListItemsLink + "/{userId}/done", USER_ID)
                        .principal(principal)
                        .param("itemId", ITEM_ID.toString()))
                .andExpect(status().isOk());
        verify(customShoppingListItemService).updateItemStatusToDone(USER_ID, ITEM_ID);
    }

    @Test
    @DisplayName("Delete custom shopping list items for a specific user with multiple items")
    void bulkDeleteCustomShoppingListItems_WithMultipleItems_ReturnsOk() throws Exception {
        String toDelete = "1,2,3";
        mockMvc.perform(delete(customShoppingListItemsLink + "/{userId}/custom-shopping-list-items", USER_ID)
                        .principal(principal)
                        .param("ids", toDelete))
                .andExpect(status().isOk());
        verify(customShoppingListItemService).bulkDelete(toDelete);
    }
}
