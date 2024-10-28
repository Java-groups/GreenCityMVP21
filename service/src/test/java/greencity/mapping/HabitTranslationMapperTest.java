package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitTranslationMapperTest {
    private HabitTranslationMapper habitTranslationMapper;

    @BeforeEach
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        habitTranslationMapper = new HabitTranslationMapper();
        modelMapper.addConverter(habitTranslationMapper);
    }

    @Test
    public void testConvertSingleHabitTranslationDto() {
        HabitTranslationDto dto = createHabitTranslationDto("Habit Name", "Description", "Item", "en");

        HabitTranslation result = habitTranslationMapper.convert(dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getHabitItem(), result.getHabitItem());
    }

    @Test
    public void testMapAllToList() {
        List<HabitTranslationDto> dtoList = new ArrayList<>();
        dtoList.add(createHabitTranslationDto("Habit 1", "Description 1", "Item 1", "en"));
        dtoList.add(createHabitTranslationDto("Habit 2", "Description 2", "Item 2", "ua"));

        List<HabitTranslation> resultList = habitTranslationMapper.mapAllToList(dtoList);

        assertNotNull(resultList);
        assertEquals(dtoList.size(), resultList.size());

        for (int i = 0; i < dtoList.size(); i++) {
            assertEquals(dtoList.get(i).getName(), resultList.get(i).getName());
            assertEquals(dtoList.get(i).getDescription(), resultList.get(i).getDescription());
            assertEquals(dtoList.get(i).getHabitItem(), resultList.get(i).getHabitItem());
        }
    }

    @Test
    public void testMapAllToListWithEmptyList() {
        List<HabitTranslationDto> dtoList = new ArrayList<>();

        List<HabitTranslation> resultList = habitTranslationMapper.mapAllToList(dtoList);

        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());
    }

    private HabitTranslationDto createHabitTranslationDto(String name, String description, String habitItem, String languageCode) {
        return HabitTranslationDto.builder()
                .name(name)
                .description(description)
                .habitItem(habitItem)
                .languageCode(languageCode)
                .build();
    }
}
