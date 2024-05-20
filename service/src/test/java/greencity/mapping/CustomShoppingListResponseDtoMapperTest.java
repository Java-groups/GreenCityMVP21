package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListResponseDtoMapperTest {

    @InjectMocks
    CustomShoppingListResponseDtoMapper mapper;

    @Test
    @DisplayName("Test mapping from CustomShoppingListItem to CustomShoppingListItemResponseDto")
    void convert() {

        CustomShoppingListItem customShoppingListItem = CustomShoppingListItem.builder()
                .id(1L)
                .text("Buy milk")
                .status(ShoppingListItemStatus.ACTIVE)
                .dateCompleted(LocalDateTime.now())
                .build();

        CustomShoppingListItemResponseDto dto = mapper.convert(customShoppingListItem);

        assert dto != null;

        assertEquals(dto.getId(), customShoppingListItem.getId());
        assertEquals(dto.getText(), customShoppingListItem.getText());
        assertEquals(dto.getStatus(), customShoppingListItem.getStatus());


    }

    @Test
    @DisplayName("Test mapping List of CustomShoppingListItem to List of CustomShoppingListItemResponseDto")
    void mapAllToList() {

        CustomShoppingListItem customShoppingListItem1 = CustomShoppingListItem.builder()
                .id(1L)
                .text("Buy milk1")
                .status(ShoppingListItemStatus.ACTIVE)
                .dateCompleted(LocalDateTime.now())
                .build();

        CustomShoppingListItem customShoppingListItem2 = CustomShoppingListItem.builder()
                .id(1L)
                .text("Buy milk2")
                .status(ShoppingListItemStatus.DISABLED)
                .dateCompleted(LocalDateTime.now())
                .build();

        List<CustomShoppingListItem> listToConvert = new ArrayList<>();
        listToConvert.add(customShoppingListItem1);
        listToConvert.add(customShoppingListItem2);

        List<CustomShoppingListItemResponseDto> toVerify = mapper.mapAllToList(listToConvert);


        assert ! toVerify.contains(null);
        assertEquals (toVerify.size() , 2);
        assertEquals(toVerify.get(0).getText(), customShoppingListItem1.getText());
        assertEquals(toVerify.get(1).getText(), customShoppingListItem2.getText());

        assertEquals(toVerify.get(0).getStatus(), ShoppingListItemStatus.ACTIVE);
        assertEquals(toVerify.get(1).getStatus(), ShoppingListItemStatus.DISABLED);
    }
}