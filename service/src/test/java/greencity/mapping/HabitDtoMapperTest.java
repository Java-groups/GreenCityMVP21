package greencity.mapping;

import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.TagTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HabitDtoMapperTest {

    @Test
    public void testConvert() {
        Language language = new Language(1L, "en", null, null);

        TagTranslation tagTranslation = TagTranslation.builder()
                .id(1L)
                .name("Test Tag")
                .language(language)
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .tagTranslations(List.of(tagTranslation))
                .build();

        ShoppingListItemTranslation shoppingListItemTranslation = ShoppingListItemTranslation.builder()
                .id(1L)
                .content("Test Shopping List Item")
                .language(language)
                .build();

        ShoppingListItem shoppingListItem = ShoppingListItem.builder()
                .id(1L)
                .translations(List.of(shoppingListItemTranslation))
                .build();

        Habit habit = Habit.builder()
                .id(1L)
                .image("test-image.jpg")
                .defaultDuration(30)
                .complexity(2)
                .tags(Set.of(tag))
                .shoppingListItems(Set.of(shoppingListItem))
                .build();

        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("Test Habit")
                .description("Test Description")
                .habitItem("Test Item")
                .language(language)
                .habit(habit)
                .build();

        HabitDtoMapper mapper = new HabitDtoMapper();

        HabitDto habitDto = mapper.convert(habitTranslation);

        assertNotNull(habitDto);
        assertEquals(habit.getId(), habitDto.getId());
        assertEquals(habit.getImage(), habitDto.getImage());
        assertEquals(habit.getDefaultDuration(), habitDto.getDefaultDuration());
        assertEquals(habit.getComplexity(), habitDto.getComplexity());
        assertNotNull(habitDto.getHabitTranslation());
        assertEquals(habitTranslation.getName(), habitDto.getHabitTranslation().getName());
        assertEquals(habitTranslation.getDescription(), habitDto.getHabitTranslation().getDescription());
        assertEquals(habitTranslation.getHabitItem(), habitDto.getHabitTranslation().getHabitItem());
        assertEquals(language.getCode(), habitDto.getHabitTranslation().getLanguageCode());
        assertNotNull(habitDto.getTags());
        assertEquals(1, habitDto.getTags().size());
        assertEquals("Test Tag", habitDto.getTags().get(0));
        assertNotNull(habitDto.getShoppingListItems());
        assertEquals(1, habitDto.getShoppingListItems().size());
        ShoppingListItemDto shoppingListItemDto = habitDto.getShoppingListItems().get(0);
        assertEquals(shoppingListItem.getId(), shoppingListItemDto.getId());
        assertEquals("Test Shopping List Item", shoppingListItemDto.getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), shoppingListItemDto.getStatus());
    }
}
