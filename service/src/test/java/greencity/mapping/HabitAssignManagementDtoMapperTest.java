package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.HabitAssign;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class HabitAssignManagementDtoMapperTest {
    @InjectMocks
    private HabitAssignManagementDtoMapper habitAssignManagementDtoMapper;
    @Test
    public void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitAssignManagementDto habitAssignManagementDto = habitAssignManagementDtoMapper.convert(habitAssign);
        assertEquals(habitAssignManagementDto.getId(), habitAssign.getId());
        assertEquals(habitAssignManagementDto.getStatus(), habitAssign.getStatus());
        assertEquals(habitAssignManagementDto.getCreateDateTime(), habitAssign.getCreateDate());
        assertEquals(habitAssignManagementDto.getUserId(), habitAssign.getUser().getId());
        assertEquals(habitAssignManagementDto.getHabitId(), habitAssign.getHabit().getId());
        assertEquals(habitAssignManagementDto.getDuration(), habitAssign.getDuration());
        assertEquals(habitAssignManagementDto.getHabitStreak(), habitAssign.getHabitStreak());
        assertEquals(habitAssignManagementDto.getWorkingDays(), habitAssign.getWorkingDays());
        assertEquals(habitAssignManagementDto.getLastEnrollment(), habitAssign.getLastEnrollmentDate());
    }
}
