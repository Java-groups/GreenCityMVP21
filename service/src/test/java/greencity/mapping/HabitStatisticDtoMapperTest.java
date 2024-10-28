package greencity.mapping;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.enums.HabitRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HabitStatisticDtoMapperTest {
    private HabitStatisticDtoMapper habitStatisticDtoMapper;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        habitStatisticDtoMapper = new HabitStatisticDtoMapper();
        modelMapper.addConverter(habitStatisticDtoMapper);
    }

    @Test
    public void testConvertHabitStatisticToDto() {
        HabitStatistic habitStatistic = createHabitStatistic(1L, HabitRate.GOOD, ZonedDateTime.now(), 10, 2L);

        HabitStatisticDto result = habitStatisticDtoMapper.convert(habitStatistic);

        assertNotNull(result);
        assertEquals(habitStatistic.getId(), result.getId());
        assertEquals(habitStatistic.getHabitRate(), result.getHabitRate());
        assertEquals(habitStatistic.getCreateDate(), result.getCreateDate());
        assertEquals(habitStatistic.getAmountOfItems(), result.getAmountOfItems());
        assertEquals(habitStatistic.getHabitAssign().getId(), result.getHabitAssignId());
    }

    private HabitStatistic createHabitStatistic(Long id, HabitRate habitRate, ZonedDateTime createDate, Integer amountOfItems, Long habitAssignId) {
        HabitAssign habitAssign = HabitAssign.builder()
                .id(habitAssignId)
                .build();

        return HabitStatistic.builder()
                .id(id)
                .habitRate(habitRate)
                .createDate(createDate)
                .amountOfItems(amountOfItems)
                .habitAssign(habitAssign)
                .build();
    }
}
