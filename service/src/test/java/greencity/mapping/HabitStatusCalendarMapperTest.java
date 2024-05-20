package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HabitStatusCalendarMapperTest {

    @InjectMocks
    private HabitStatusCalendarMapper mapper;

    @Test
    @DisplayName("Test convert from HabitStatusCalendarVO to HabitStatusCalendar")
    void convert() {
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
                .id(1L)
                .enrollDate(LocalDate.now())
                .habitAssignVO(HabitAssignVO.builder()
                        .id(1L)
                        .build())
                .build();

        HabitStatusCalendar result = mapper.convert(habitStatusCalendarVO);

        assertNotNull(result);
        assertNotNull(result.getHabitAssign());
        assertEquals(habitStatusCalendarVO.getId(), result.getId());
        assertEquals(habitStatusCalendarVO.getEnrollDate(), result.getEnrollDate());
        assertEquals(habitStatusCalendarVO.getHabitAssignVO().getId(), result.getHabitAssign().getId());
    }
}
