package greencity.mapping;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomHabitMapperTest {
    private CustomHabitMapper customHabitMapper;

    @BeforeEach
    void setUp() {
        customHabitMapper = new CustomHabitMapper();
    }

    @Test
    void convert_newObject() {
        assertEquals(new Habit().setIsCustomHabit(true), customHabitMapper.convert(new AddCustomHabitDtoRequest()));
    }

    @Test
    void convert_filledObject() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = AddCustomHabitDtoRequest.builder()
                .image("Test image")
                .complexity(101)
                .defaultDuration(102)
                .build();
        Habit expectedHabit = Habit.builder()
                .image("Test image")
                .complexity(101)
                .defaultDuration(102)
                .isCustomHabit(true)
                .build();
        assertEquals(expectedHabit, customHabitMapper.convert(addCustomHabitDtoRequest));
    }
}