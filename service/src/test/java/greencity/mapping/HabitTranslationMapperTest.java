package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HabitTranslationMapperTest {

    HabitTranslationMapper habitTranslationMapper = new HabitTranslationMapper();

    HabitTranslation habitTranslation;
    HabitTranslationDto habitTranslationDto;

    @BeforeEach
    void setUp() {
        habitTranslationDto = HabitTranslationDto.builder()
                .name("Test Habit")
                .habitItem("Test Item")
                .description("Test Description")
                .build();

        habitTranslation = HabitTranslation.builder()
                .name(habitTranslationDto.getName())
                .description(habitTranslationDto.getDescription())
                .habitItem(habitTranslationDto.getHabitItem())
                .build();
    }

    @Test
    public void testConvert() {
        Assertions.assertEquals(habitTranslation, habitTranslationMapper.convert(habitTranslationDto));
    }

    @Test
    public void testMapToList() {
        Assertions.assertEquals(List.of(habitTranslation), habitTranslationMapper.mapAllToList(List.of(habitTranslationDto)));
    }
}