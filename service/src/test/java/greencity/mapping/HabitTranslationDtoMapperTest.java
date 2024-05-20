package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HabitTranslationDtoMapperTest {

    HabitTranslationDtoMapper habitTranslationDtoMapper = new HabitTranslationDtoMapper();

    HabitTranslation habitTranslation;
    HabitTranslationDto habitTranslationDto;

    @BeforeEach
    void setUp() {
        Language language = Language.builder()
                .code("EN")
                .build();

        habitTranslation = HabitTranslation.builder()
                .name("Test Habit")
                .description("Test Description")
                .habitItem("Test Item")
                .language(language)
                .build();

        habitTranslationDto = HabitTranslationDto.builder()
                .name(habitTranslation.getName())
                .languageCode(habitTranslation.getLanguage().getCode())
                .habitItem(habitTranslation.getHabitItem())
                .description(habitTranslation.getDescription())
                .build();

    }

    @Test
    public void testConvert() {
        Assertions.assertEquals(habitTranslationDto, habitTranslationDtoMapper.convert(habitTranslation));
    }

    @Test
    public void testMapToList() {
        Assertions.assertEquals(List.of(habitTranslationDto), habitTranslationDtoMapper.mapAllToList(List.of(habitTranslation)));
    }
}