package greencity.controller;

import greencity.config.SecurityConfig;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getCustomShoppingListItemResponseDto;
import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
class CustomShoppingListItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;

    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;
    CustomShoppingListItemResponseDto responseDto;
    private Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Get all available custom shopping list items for a specific user")
    void getAllAvailableCustomShoppingListItems_ShouldReturnOkResponse() {
        Long userId = 1L;
        Long habitId = 1L;

        CustomShoppingListItemResponseDto item = new CustomShoppingListItemResponseDto();
        List<CustomShoppingListItemResponseDto> shoppingListItems = Collections.singletonList(item);

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userId, habitId))
                .thenReturn(shoppingListItems);

        ResponseEntity<List<CustomShoppingListItemResponseDto>> response =
                customShoppingListItemController.getAllAvailableCustomShoppingListItems(userId, habitId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shoppingListItems, response.getBody());

        verify(customShoppingListItemService, times(1)).findAllAvailableCustomShoppingListItems(userId, habitId);
    }

    @Test
    @DisplayName("Save user custom shopping list items")
    void testSaveUserCustomShoppingListItems() throws Exception {
        String requestJson = "{ \"items\": [] }";

        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto();
        responseDto.setId(1L);

        when(customShoppingListItemService.save(any(), any(), any()))
                .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/custom/shopping-list-items/{userId}/{habitAssignId}/custom-shopping-list-items", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @DisplayName("Update the status of a specific custom shopping list item")
    void testUpdateItemStatus() throws Exception {
        when(customShoppingListItemService.updateItemStatus(1L, 1L, "DONE"))
                .thenReturn(new CustomShoppingListItemResponseDto());

        mockMvc.perform(patch("/custom/shopping-list-items/{itemId}/custom-shopping-list-items", 1)
                        .param("itemId", "1")
                        .param("status", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @DisplayName("Mark a specific shopping list item as done")
    void testUpdateItemStatusToDone() throws Exception {
        Mockito.doNothing().when(customShoppingListItemService).updateItemStatusToDone(1L, 1L);

        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/done",1)
                        .param("itemId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete custom shopping list items for a specific user")
    void bulkDeleteCustomShoppingListItems_WithMultipleItems_ReturnsOk() throws Exception {
        String ids = "1,2,3";
        mockMvc.perform(delete("/custom/shopping-list-items/{userId}/custom-shopping-list-items", 1L)
                        .principal(principal)
                        .param("ids", ids))
                .andExpect(status().isOk());
        verify(customShoppingListItemService).bulkDelete(ids);
    }

    @Test
    @DisplayName("Get all custom shopping items by status")
    void getAllCustomShoppingItemsByStatus_ForSpecificUser_ReturnsOk() throws Exception {
        responseDto = getCustomShoppingListItemResponseDto();
        mockMvc.perform(get("/custom/shopping-list-items/{userId}/custom-shopping-list-items", 1L)
                        .principal(principal))
                .andExpect(status().isOk());
        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(anyLong(), anyString()))
                .thenReturn(List.of(responseDto));
        assertEquals(responseDto, customShoppingListItemController.getAllCustomShoppingItemsByStatus(1L, "ACTIVE")
                .getBody().getFirst());
    }
}
