package greencity.controller;

import greencity.config.SecurityConfig;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController).build();
    }

    @Test
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
}