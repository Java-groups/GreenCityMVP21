package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemTranslationDTO;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShoppingListItemResponseDtoMapperTest {
    private ShoppingListItemResponseDtoMapper shoppingListItemResponseDtoMapper;

    @BeforeEach
    void setUp() {
        shoppingListItemResponseDtoMapper = new ShoppingListItemResponseDtoMapper();
    }

    @Test
    void testConvert() {
        ShoppingListItemTranslation translation1 = ShoppingListItemTranslation.builder()
                .id(1L)
                .content("Milk")
                .build();
        ShoppingListItemTranslation translation2 = ShoppingListItemTranslation.builder()
                .id(2L)
                .content("Bread")
                .build();
        List<ShoppingListItemTranslation> translations = Arrays.asList(translation1, translation2);

        ShoppingListItem shoppingListItem = ShoppingListItem.builder()
                .id(1L)
                .translations(translations)
                .build();

        ShoppingListItemResponseDto responseDto = shoppingListItemResponseDtoMapper.convert(shoppingListItem);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertNotNull(responseDto.getTranslations());
        assertEquals(2, responseDto.getTranslations().size());

        ShoppingListItemTranslationDTO dto1 = responseDto.getTranslations().get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Milk", dto1.getContent());

        ShoppingListItemTranslationDTO dto2 = responseDto.getTranslations().get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Bread", dto2.getContent());
    }
}
