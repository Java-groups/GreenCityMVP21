package greencity.mapping;

import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HabitAssignUserDurationDtoMapperTest {

    @Test
    public void testConvert() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .build();

        Habit habit = Habit.builder()
                .id(1L)
                .image("test-image.jpg")
                .build();

        HabitAssign habitAssign = HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .duration(30)
                .workingDays(15)
                .habitStreak(10)
                .lastEnrollmentDate(ZonedDateTime.now())
                .user(user)
                .habit(habit)
                .build();

        HabitAssignUserDurationDtoMapper mapper = new HabitAssignUserDurationDtoMapper();

        HabitAssignUserDurationDto dto = mapper.convert(habitAssign);

        assertNotNull(dto);
        assertEquals(habitAssign.getId(), dto.getHabitAssignId());
        assertEquals(habitAssign.getUser().getId(), dto.getUserId());
        assertEquals(habitAssign.getHabit().getId(), dto.getHabitId());
        assertEquals(habitAssign.getStatus(), dto.getStatus());
        assertEquals(habitAssign.getWorkingDays(), dto.getWorkingDays());
        assertEquals(habitAssign.getDuration(), dto.getDuration());
    }
}
