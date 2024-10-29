package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.HabitAssign;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitAssignMapperTest {
    @InjectMocks
    private HabitAssignMapper habitAssignMapper;
    @Test
    public void convertTest() {
        HabitAssignDto habitAssignDto = getHabitAssignDto();
        HabitAssign habitAssign = habitAssignMapper.convert(habitAssignDto);
        assertEquals(habitAssign.getId(), habitAssignDto.getId());
        assertEquals(habitAssign.getDuration(), habitAssignDto.getDuration());
        assertEquals(habitAssign.getHabitStreak(), habitAssignDto.getHabitStreak());
        assertEquals(habitAssign.getCreateDate(), habitAssignDto.getCreateDateTime());
        assertEquals(habitAssign.getStatus(), habitAssignDto.getStatus());
        assertEquals(habitAssign.getWorkingDays(), habitAssignDto.getWorkingDays());
        assertEquals(habitAssign.getLastEnrollmentDate(), habitAssignDto.getLastEnrollmentDate());
        assertEquals(habitAssign.getHabit().getId(), habitAssignDto.getHabit().getId());
        assertEquals(habitAssign.getHabit().getComplexity(), habitAssignDto.getHabit().getComplexity());
        assertEquals(habitAssign.getHabit().getDefaultDuration(), habitAssignDto.getDuration());
        assertEquals(habitAssign.getHabit().getId(), habitAssignDto.getHabit().getId());
    }

    private HabitAssignDto getHabitAssignDto() {
        return HabitAssignDto.builder()
                .createDateTime(ZonedDateTime.now())
                .duration(30)
                .habit(HabitDto.builder()
                        .id(1L)
                        .build())
                .userShoppingListItems(List.of(
                        UserShoppingListItemAdvanceDto.builder()
                                .id(1L)
                                .shoppingListItemId(101L)
                                .status(ShoppingListItemStatus.INPROGRESS)
                                .dateCompleted(LocalDateTime.now())
                                .content("Buy organic vegetables")
                                .build()
                ))
                .habitStatusCalendarDtoList(List.of(
                        HabitStatusCalendarDto.builder()
                                .enrollDate(LocalDate.now())
                                .id(1L)
                                .build()
                ))
                .habitStreak(5)
                .id(1L)
                .lastEnrollmentDate(ZonedDateTime.now().minusDays(1))
                .status(HabitAssignStatus.ACTIVE)
                .userId(1001L)
                .workingDays(20)
                .progressNotificationHasDisplayed(false)
                .build();
    }
}
