package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.CustomShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static greencity.ModelUtils.*;
import static greencity.enums.ShoppingListItemStatus.ACTIVE;
import static greencity.enums.ShoppingListItemStatus.DONE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomShoppingListItemControllerTest {
    private static final String BASE_URL = "/custom/shopping-list-items";
    private MockMvc mockMvc;
    @Mock
    private CustomShoppingListItemService customShoppingListItemService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;
    private Principal principal;
    private UserVO userVO;

    @BeforeEach
    void setUp() {
        userVO = getUserVO();
        principal = getPrincipal();

        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);
    }

    @Test
    void getAllAvailableCustomShoppingListItems() throws Exception {
        Long habitId = 1L;
        Long userId = 5L;
        List<CustomShoppingListItemResponseDto> itemList = getCustomShoppingListItemResponseDtos();

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(anyLong(), anyLong())).
                thenReturn(itemList);

        this.mockMvc.perform(get(BASE_URL + "/{userId}/{habitId}", userId, habitId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].text").value("text1"))
                .andExpect(jsonPath("$[0].status").value(ACTIVE.toString()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].text").value("text2"))
                .andExpect(jsonPath("$[1].status").value(DONE.toString()));

        verify(customShoppingListItemService).findAllAvailableCustomShoppingListItems(anyLong(), anyLong());
    }


    @Test
    void saveUserCustomShoppingListItems() throws Exception {
        Long habitAssignId = 1L;
        Long userId = 5L;
        List<CustomShoppingListItemResponseDto> itemList = getCustomShoppingListItemResponseDtos();

        String requestJson = """
            {
              "customShoppingListItemSaveRequestDtoList": [
                { "text": "text1" },
                { "text": "text2" }
              ]
            }
            """;

        when(customShoppingListItemService.save(
                any(BulkSaveCustomShoppingListItemDto.class),
                eq(userId),
                eq(habitAssignId)))
                .thenReturn(itemList);

        this.mockMvc.perform(post(BASE_URL + "/{userId}/{habitAssignId}/custom-shopping-list-items",
                userId, habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].text").value("text1"))
                .andExpect(jsonPath("$[0].status").value(ACTIVE.toString()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].text").value("text2"))
                .andExpect(jsonPath("$[1].status").value(DONE.toString()));

        verify(customShoppingListItemService).save(
                any(BulkSaveCustomShoppingListItemDto.class),
                eq(userId), eq(habitAssignId));
    }

    @Test
    void updateItemStatus() throws Exception {
        Long userId = 1L;
        Long itemId = 5L;
        String itemStatus = ACTIVE.toString();
        CustomShoppingListItemResponseDto dto = getCustomShoppingListItemResponseDto();

        when(customShoppingListItemService.updateItemStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(dto);

        this.mockMvc.perform(patch(BASE_URL + "/{userId}/custom-shopping-list-items", userId)
                        .param("itemId", String.valueOf(itemId))
                        .param("status", itemStatus)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.status").value(itemStatus));

        verify(customShoppingListItemService).updateItemStatus(anyLong(), anyLong(), anyString());
    }

    @Test
    void updateItemStatusToDone() throws Exception {
        Long userId = 1L;
        Long itemId = 5L;

        doNothing().when(customShoppingListItemService).updateItemStatusToDone(userId, itemId);

        this.mockMvc.perform(patch(BASE_URL + "/{userId}/done", userId)
                        .param("itemId", String.valueOf(itemId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customShoppingListItemService).updateItemStatusToDone(anyLong(), anyLong());
    }

    @Test
    void bulkDeleteCustomShoppingListItems() throws Exception {
        Long userId = 1L;
        String ids = "1,2,3";
        List<Long> idList = List.of(1L, 2L, 3L);

        when(customShoppingListItemService.bulkDelete(ids)).thenReturn(eq(idList));

        this.mockMvc.perform(delete(BASE_URL + "/{userId}/custom-shopping-list-items", userId)
                        .param("ids", ids)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customShoppingListItemService).bulkDelete(ids);
    }

    @Test
    void getAllCustomShoppingItemsByStatus() throws Exception {
        Long userId = 1L;
        String status = DONE.toString();
        List<CustomShoppingListItemResponseDto> itemList = getCustomShoppingListItemResponseDtos();

        when(customShoppingListItemService
                .findAllUsersCustomShoppingListItemsByStatus(anyLong(), anyString()))
                .thenReturn(itemList);

        this.mockMvc.perform(get(BASE_URL + "/{userId}/custom-shopping-list-items", userId)
                        .param("status", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(anyLong(), anyString());
    }

    private static List<CustomShoppingListItemResponseDto> getCustomShoppingListItemResponseDtos() {
        return List.of(
                CustomShoppingListItemResponseDto.builder()
                        .id(1L)
                        .status(ACTIVE)
                        .text("text1")
                        .build(),
                CustomShoppingListItemResponseDto.builder()
                        .id(2L)
                        .status(DONE)
                        .text("text2")
                        .build()
        );
    }
}