package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShoppingListItemDtoMapperTest {
    private ShoppingListItemDtoMapper shoppingListItemDtoMapper;

    @BeforeEach
    void setUp() {
        shoppingListItemDtoMapper = new ShoppingListItemDtoMapper();
    }

    @Test
    void testConvert() {
        ShoppingListItem shoppingListItem = ShoppingListItem.builder().id(1L).build();
        ShoppingListItemTranslation translation = ShoppingListItemTranslation.builder()
                .shoppingListItem(shoppingListItem)
                .content("Milk")
                .build();

        ShoppingListItemDto dto = shoppingListItemDtoMapper.convert(translation);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Milk", dto.getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), dto.getStatus());
    }
}
