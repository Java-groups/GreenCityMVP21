package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HabitStatusCalendarMapperTest {
    private HabitStatusCalendarMapper habitStatusCalendarMapper;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        habitStatusCalendarMapper = new HabitStatusCalendarMapper();
        modelMapper.addConverter(habitStatusCalendarMapper);
    }

    @Test
    public void testConvertHabitStatusCalendarVOToEntity() {
        HabitStatusCalendarVO habitStatusCalendarVO = createHabitStatusCalendarVO(1L, LocalDate.of(2024, 10, 22), 2L);

        HabitStatusCalendar result = habitStatusCalendarMapper.convert(habitStatusCalendarVO);

        assertNotNull(result);
        assertEquals(habitStatusCalendarVO.getId(), result.getId());
        assertEquals(habitStatusCalendarVO.getEnrollDate(), result.getEnrollDate());
        assertNotNull(result.getHabitAssign());
        assertEquals(habitStatusCalendarVO.getHabitAssignVO().getId(), result.getHabitAssign().getId());
    }

    private HabitStatusCalendarVO createHabitStatusCalendarVO(Long id, LocalDate enrollDate, Long habitAssignId) {
        return HabitStatusCalendarVO.builder()
                .id(id)
                .enrollDate(enrollDate)
                .habitAssignVO(HabitAssignVO.builder()
                        .id(habitAssignId)
                        .build())
                .build();
    }
}
