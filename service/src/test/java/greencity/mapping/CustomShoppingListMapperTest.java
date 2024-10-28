package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomShoppingListMapperTest {
    private CustomShoppingListMapper customShoppingListMapper;

    @BeforeEach
    void setUp() {
        customShoppingListMapper = new CustomShoppingListMapper();
    }

    @Test
    void convert_newObject() {
        assertEquals(new CustomShoppingListItem().setStatus(null), customShoppingListMapper.convert(new CustomShoppingListItemResponseDto()));
    }

    @Test
    void convert_filledObject() {
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto = CustomShoppingListItemResponseDto.builder()
                .id(101L)
                .text("Test text")
                .status(ShoppingListItemStatus.DONE)
                .build();
        CustomShoppingListItem expectedCustomShoppingListItem = CustomShoppingListItem.builder()
                .id(101L)
                .text("Test text")
                .status(ShoppingListItemStatus.DONE)
                .build();
        assertEquals(expectedCustomShoppingListItem, customShoppingListMapper.convert(customShoppingListItemResponseDto));
    }

    @Test
    void mapAllToList_emptyList() {
        assertTrue(customShoppingListMapper.mapAllToList(new ArrayList<CustomShoppingListItemResponseDto>()).isEmpty());
    }

    @Test
    void mapAllToList_filledList() {
        List<CustomShoppingListItemResponseDto> customShoppingListItemResponseDtos = List.of(
                CustomShoppingListItemResponseDto.builder()
                        .id(101L)
                        .text("Test text 1")
                        .status(ShoppingListItemStatus.DONE)
                        .build(),
                CustomShoppingListItemResponseDto.builder()
                        .id(102L)
                        .text("Test text 2")
                        .status(ShoppingListItemStatus.INPROGRESS)
                        .build()
        );
        List<CustomShoppingListItem> expectedCustomShoppingListItems = List.of(
                CustomShoppingListItem.builder()
                        .id(101L)
                        .text("Test text 1")
                        .status(ShoppingListItemStatus.DONE)
                        .build(),
                CustomShoppingListItem.builder()
                        .id(102L)
                        .text("Test text 2")
                        .status(ShoppingListItemStatus.INPROGRESS)
                        .build()
        );

        assertEquals(2, customShoppingListMapper.mapAllToList(customShoppingListItemResponseDtos).size());
        assertEquals(expectedCustomShoppingListItems.get(0), customShoppingListMapper.convert(customShoppingListItemResponseDtos.get(0)));
        assertEquals(expectedCustomShoppingListItems.get(1), customShoppingListMapper.convert(customShoppingListItemResponseDtos.get(1)));
    }
}