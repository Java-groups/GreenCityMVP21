package greencity.mapping;

import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitManagementDtoMapperTest {
    private HabitManagementDtoMapper habitManagementDtoMapper;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        habitManagementDtoMapper = new HabitManagementDtoMapper();
        modelMapper.addConverter(habitManagementDtoMapper);
    }

    @Test
    public void testConvertHabitToHabitManagementDto() {
        Habit habit = createHabit();

        HabitManagementDto result = habitManagementDtoMapper.convert(habit);

        assertNotNull(result);
        assertEquals(habit.getId(), result.getId());
        assertEquals(habit.getImage(), result.getImage());
        assertEquals(habit.getComplexity(), result.getComplexity());
        assertEquals(habit.getDefaultDuration(), result.getDefaultDuration());

        // Проверка списка переводов привычек
        assertNotNull(result.getHabitTranslations());
        assertEquals(habit.getHabitTranslations().size(), result.getHabitTranslations().size());

        HabitTranslationManagementDto habitTranslationDto = result.getHabitTranslations().get(0);
        HabitTranslation habitTranslation = habit.getHabitTranslations().get(0);
        assertEquals(habitTranslation.getId(), habitTranslationDto.getId());
        assertEquals(habitTranslation.getDescription(), habitTranslationDto.getDescription());
        assertEquals(habitTranslation.getHabitItem(), habitTranslationDto.getHabitItem());
        assertEquals(habitTranslation.getName(), habitTranslationDto.getName());
        assertEquals(habitTranslation.getLanguage().getCode(), habitTranslationDto.getLanguageCode());
    }

    private Habit createHabit() {
        Language language = Language.builder()
                .id(1L)
                .code("en")
                .build();

        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("Test Habit")
                .description("Test Description")
                .habitItem("Test Item")
                .language(language)
                .build();

        return Habit.builder()
                .id(1L)
                .image("image_url")
                .complexity(2)
                .defaultDuration(30)
                .habitTranslations(List.of(habitTranslation))
                .build();
    }
}
