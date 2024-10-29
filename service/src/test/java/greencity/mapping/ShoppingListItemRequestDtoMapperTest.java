package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShoppingListItemRequestDtoMapperTest {
    private ShoppingListItemRequestDtoMapper shoppingListItemRequestDtoMapper;

    @BeforeEach
    void setUp() {
        shoppingListItemRequestDtoMapper = new ShoppingListItemRequestDtoMapper();
    }

    @Test
    void testConvert() {
        ShoppingListItemRequestDto requestDto = ShoppingListItemRequestDto.builder()
                .id(1L)
                .build();

        UserShoppingListItem userShoppingListItem = shoppingListItemRequestDtoMapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(1L, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, userShoppingListItem.getStatus());
    }
}
