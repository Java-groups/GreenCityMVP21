package greencity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitstatistic.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HabitStatisticControllerTest {
    private final String HABIT_STATISTIC_CONTROLLER_URL = "/habit/statistic";
    private final List<HabitStatisticDto> HABIT_STATISTIC_DTOS = List.of(
            HabitStatisticDto.builder()
                    .id(101L)
                    .build(),
            HabitStatisticDto.builder()
                    .id(102L)
                    .build()
    );
    private MockMvc mockMvc;

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    @Mock
    private HabitStatisticService habitStatisticService;

    @Mock
    private Validator validator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .setValidator(validator)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void findAllByHabitId() throws Exception {
        GetHabitStatisticDto getHabitStatisticDto = GetHabitStatisticDto.builder()
                .habitStatisticDtoList(HABIT_STATISTIC_DTOS)
                .amountOfUsersAcquired(2L)
                .build();

        when(habitStatisticService.findAllStatsByHabitId(anyLong())).thenReturn(getHabitStatisticDto);

        mockMvc.perform(get(HABIT_STATISTIC_CONTROLLER_URL + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amountOfUsersAcquired").value(2L))
                .andExpect(jsonPath("$.habitStatisticDtoList[0].id").value(101L))
                .andExpect(jsonPath("$.habitStatisticDtoList[1].id").value(102L));

        verify(habitStatisticService, times(1)).findAllStatsByHabitId(1L);
    }

    @Test
    void findAllStatsByHabitAssignId() throws Exception {
        when(habitStatisticService.findAllStatsByHabitAssignId(anyLong())).thenReturn(HABIT_STATISTIC_DTOS);

        mockMvc.perform(get(HABIT_STATISTIC_CONTROLLER_URL + "/assign/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(101L))
                .andExpect(jsonPath("$[1].id").value(102L));

        verify(habitStatisticService, times(1)).findAllStatsByHabitAssignId(1L);
    }

    @Test
    void saveHabitStatistic() throws Exception {

        AddHabitStatisticDto addHabitStatisticDto = new AddHabitStatisticDto();
        UserVO userVO = new UserVO().setId(101L).setEmail("email@email.com");

        when(habitStatisticService.saveByHabitIdAndUserId(anyLong(), anyLong(), any(AddHabitStatisticDto.class))).thenReturn(HABIT_STATISTIC_DTOS.getFirst());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(HABIT_STATISTIC_CONTROLLER_URL + "/102")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addHabitStatisticDto))
                        .principal(userVO::getEmail)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(101L));

        verify(habitStatisticService, times(1)).saveByHabitIdAndUserId(102L, 101L, addHabitStatisticDto);
    }

    @Test
    void updateStatistic() throws Exception {
        UpdateHabitStatisticDto updateHabitStatisticDto =  UpdateHabitStatisticDto.builder()
                .amountOfItems(103)
                .habitRate(HabitRate.DEFAULT)
                .build();
        UserVO userVO = UserVO.builder()
                .id(101L)
                .email("email@email.com")
                .build();

        when(habitStatisticService.update(anyLong(), anyLong(), any(UpdateHabitStatisticDto.class))).thenReturn(updateHabitStatisticDto);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(HABIT_STATISTIC_CONTROLLER_URL + "/102")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateHabitStatisticDto))
                        .principal(userVO::getEmail)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amountOfItems").value(103))
                .andExpect(jsonPath("$.habitRate").value("DEFAULT"));

        verify(habitStatisticService, times(1)).update(102L, 101L, updateHabitStatisticDto);
    }

    @Test
    void getTodayStatisticsForAllHabitItems() throws Exception {
        List<HabitItemsAmountStatisticDto> habitItemsAmountStatisticDtos = List.of(
                new HabitItemsAmountStatisticDto("Habit Item 1", 101L),
                new HabitItemsAmountStatisticDto("Habit Item 2", 102L)
        );

        when(habitStatisticService.getTodayStatisticsForAllHabitItems(anyString())).thenReturn(habitItemsAmountStatisticDtos);

        mockMvc.perform(get(HABIT_STATISTIC_CONTROLLER_URL + "/todayStatisticsForAllHabitItems")
                        .locale(Locale.ENGLISH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].habitItem").value("Habit Item 1"))
                .andExpect(jsonPath("$.[0].notTakenItems").value(101L))
                .andExpect(jsonPath("$.[1].habitItem").value("Habit Item 2"))
                .andExpect(jsonPath("$.[1].notTakenItems").value(102L));

        verify(habitStatisticService, times(1)).getTodayStatisticsForAllHabitItems(Locale.ENGLISH.getLanguage());
    }

    @Test
    void findAmountOfAcquiredHabits() throws Exception {
        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(anyLong())).thenReturn(102L);

        mockMvc.perform(get(HABIT_STATISTIC_CONTROLLER_URL + "/acquired/count")
                        .param("userId", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("102"));

        verify(habitStatisticService, times(1)).getAmountOfAcquiredHabitsByUserId(101L);
    }

    @Test
    void findAmountOfHabitsInProgress() throws Exception {
        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(anyLong())).thenReturn(102L);

        mockMvc.perform(get(HABIT_STATISTIC_CONTROLLER_URL + "/in-progress/count")
                        .param("userId", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("102"));

        verify(habitStatisticService, times(1)).getAmountOfHabitsInProgressByUserId(101L);
    }
}