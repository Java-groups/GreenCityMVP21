package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShoppingListItemWithStatusRequestDtoMapperTest {
    private ShoppingListItemWithStatusRequestDtoMapper shoppingListItemWithStatusRequestDtoMapper;

    @BeforeEach
    void setUp() {
        shoppingListItemWithStatusRequestDtoMapper = new ShoppingListItemWithStatusRequestDtoMapper();
    }

    @Test
    void testConvert() {
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
                .id(1L)
                .status(ShoppingListItemStatus.DONE)
                .build();

        UserShoppingListItem userShoppingListItem = shoppingListItemWithStatusRequestDtoMapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(1L, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.DONE, userShoppingListItem.getStatus());
    }
}

