package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitTranslationDtoMapperTest {
    private HabitTranslationDtoMapper habitTranslationDtoMapper;

    @BeforeEach
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        habitTranslationDtoMapper = new HabitTranslationDtoMapper();
        modelMapper.addConverter(habitTranslationDtoMapper);
    }

    @Test
    public void testConvertSingleHabitTranslation() {
        HabitTranslation habitTranslation = createHabitTranslation("Habit Name", "Description", "Item", "en");

        HabitTranslationDto result = habitTranslationDtoMapper.convert(habitTranslation);

        assertNotNull(result);
        assertEquals(habitTranslation.getName(), result.getName());
        assertEquals(habitTranslation.getDescription(), result.getDescription());
        assertEquals(habitTranslation.getHabitItem(), result.getHabitItem());
        assertEquals(habitTranslation.getLanguage().getCode(), result.getLanguageCode());
    }

    @Test
    public void testMapAllToList() {
        List<HabitTranslation> habitTranslations = new ArrayList<>();
        habitTranslations.add(createHabitTranslation("Habit 1", "Description 1", "Item 1", "en"));
        habitTranslations.add(createHabitTranslation("Habit 2", "Description 2", "Item 2", "ua"));

        List<HabitTranslationDto> resultList = habitTranslationDtoMapper.mapAllToList(habitTranslations);

        assertNotNull(resultList);
        assertEquals(habitTranslations.size(), resultList.size());

        for (int i = 0; i < habitTranslations.size(); i++) {
            assertEquals(habitTranslations.get(i).getName(), resultList.get(i).getName());
            assertEquals(habitTranslations.get(i).getDescription(), resultList.get(i).getDescription());
            assertEquals(habitTranslations.get(i).getHabitItem(), resultList.get(i).getHabitItem());
            assertEquals(habitTranslations.get(i).getLanguage().getCode(), resultList.get(i).getLanguageCode());
        }
    }

    @Test
    public void testMapAllToListWithEmptyList() {
        List<HabitTranslation> habitTranslations = new ArrayList<>();

        List<HabitTranslationDto> resultList = habitTranslationDtoMapper.mapAllToList(habitTranslations);

        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());
    }

    private HabitTranslation createHabitTranslation(String name, String description, String habitItem, String languageCode) {
        Language language = Language.builder().code(languageCode).build();
        return HabitTranslation.builder()
                .name(name)
                .description(description)
                .habitItem(habitItem)
                .language(language)
                .build();
    }
}
