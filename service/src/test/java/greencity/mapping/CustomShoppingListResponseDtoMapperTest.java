package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomShoppingListResponseDtoMapperTest {
    private CustomShoppingListResponseDtoMapper mapper;
    private List<CustomShoppingListItem> customShoppingListItems;
    private List<CustomShoppingListItemResponseDto> expectedCustomShoppingListItemResponseDtos;

    @BeforeEach
    void setUp() {
        mapper = new CustomShoppingListResponseDtoMapper();
        customShoppingListItems = List.of(
                CustomShoppingListItem.builder()
                        .id(101L)
                        .text("Test text 1")
                        .status(ShoppingListItemStatus.ACTIVE)
                        .build(),
                CustomShoppingListItem.builder()
                        .id(102L)
                        .text("Test text 2")
                        .status(ShoppingListItemStatus.INPROGRESS)
                        .build()
        );
        expectedCustomShoppingListItemResponseDtos = List.of(
                CustomShoppingListItemResponseDto.builder()
                        .id(101L)
                        .text("Test text 1")
                        .status(ShoppingListItemStatus.ACTIVE)
                        .build(),
                CustomShoppingListItemResponseDto.builder()
                        .id(102L)
                        .text("Test text 2")
                        .status(ShoppingListItemStatus.INPROGRESS)
                        .build()
        );
    }

    @Test
    void convert() {
        assertEquals(expectedCustomShoppingListItemResponseDtos.getFirst(), mapper.convert(customShoppingListItems.getFirst()));
    }

    @Test
    void mapAllToList() {
        assertEquals(expectedCustomShoppingListItemResponseDtos, mapper.mapAllToList(customShoppingListItems));
    }
}