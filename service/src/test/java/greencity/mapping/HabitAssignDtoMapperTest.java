package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.entity.HabitAssign;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HabitAssignDtoMapperTest {
    @InjectMocks
    private HabitAssignDtoMapper habitAssignDtoMapper;
    @Test
    public void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitAssignDto habitAssignDto = habitAssignDtoMapper.convert(habitAssign);
        assertEquals(habitAssignDto.getId(), habitAssign.getId());
        assertEquals(habitAssignDto.getStatus(), habitAssign.getStatus());
        assertEquals(habitAssignDto.getCreateDateTime(), habitAssign.getCreateDate());
        assertEquals(habitAssignDto.getUserId(), habitAssign.getUser().getId());
        assertEquals(habitAssignDto.getDuration(), habitAssign.getDuration());
        assertEquals(habitAssignDto.getStatus(), habitAssign.getStatus());
        assertEquals(habitAssignDto.getHabitStreak(), habitAssign.getHabitStreak());
        assertEquals(habitAssignDto.getWorkingDays(), habitAssign.getWorkingDays());
        assertEquals(habitAssignDto.getLastEnrollmentDate(), habitAssign.getLastEnrollmentDate());
    }
}
