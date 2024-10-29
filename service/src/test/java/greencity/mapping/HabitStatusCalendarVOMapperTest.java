package greencity.mapping;

import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HabitStatusCalendarVOMapperTest {
    private HabitStatusCalendarVOMapper habitStatusCalendarVOMapper;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        habitStatusCalendarVOMapper = new HabitStatusCalendarVOMapper();
        modelMapper.addConverter(habitStatusCalendarVOMapper);
    }

    @Test
    public void testConvertHabitStatusCalendarToVO() {
        HabitStatusCalendar habitStatusCalendar = createHabitStatusCalendar(1L, LocalDate.of(2024, 10, 22), 2L);

        HabitStatusCalendarVO result = habitStatusCalendarVOMapper.convert(habitStatusCalendar);

        assertNotNull(result);
        assertEquals(habitStatusCalendar.getId(), result.getId());
        assertEquals(habitStatusCalendar.getEnrollDate(), result.getEnrollDate());
        assertNotNull(result.getHabitAssignVO());
        assertEquals(habitStatusCalendar.getHabitAssign().getId(), result.getHabitAssignVO().getId());
    }

    private HabitStatusCalendar createHabitStatusCalendar(Long id, LocalDate enrollDate, Long habitAssignId) {
        HabitAssign habitAssign = HabitAssign.builder().id(habitAssignId).build();
        return HabitStatusCalendar.builder()
                .id(id)
                .enrollDate(enrollDate)
                .habitAssign(habitAssign)
                .build();
    }
}
