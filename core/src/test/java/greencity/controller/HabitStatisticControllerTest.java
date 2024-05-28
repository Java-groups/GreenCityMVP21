package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
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

import java.security.Principal;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitStatisticControllerTest {
    static final String habitStatisticLink = "/habit/statistic";
    MockMvc mockMvc;

    @Mock
    HabitStatisticService habitStatisticService;

    @InjectMocks
    HabitStatisticController habitStatisticController;

    @Mock
    UserService userService;

    @Mock
    ModelMapper modelMapper;

    Principal principal = getPrincipal();
    UserVO userVO = getUserVO();

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(habitStatisticController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void findAllByHabitIdTest() throws Exception {
        mockMvc.perform(get(habitStatisticLink + "/{habitId}", 1))
                .andExpect(status().isOk());
        verify(habitStatisticService).findAllStatsByHabitId(1L);
    }

    @Test
    void findAllStatsByHabitAssignIdTest() throws Exception {
        mockMvc.perform(get(habitStatisticLink + "/assign/{habitAssignId}", 1))
                .andExpect(status().isOk());
        verify(habitStatisticService).findAllStatsByHabitAssignId(1L);
    }

    @Test
    void saveHabitStatisticTest() throws Exception {
        String addHabitStatisticDtoJson = "{\"amountOfItems\":5,\"habitRate\":\"DEFAULT\",\"createDate\":0}";
        AddHabitStatisticDto addHabitStatisticDto = objectMapper.readValue(addHabitStatisticDtoJson, AddHabitStatisticDto.class);

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitStatisticLink + "/{habitId}", 1)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addHabitStatisticDtoJson))
                .andExpect(status().isCreated());

        verify(userService).findByEmail("test@gmail.com");
        verify(habitStatisticService).saveByHabitIdAndUserId(1L, userVO.getId(), addHabitStatisticDto);
    }

    @Test
    void updateStatisticTest() throws Exception {
        UpdateHabitStatisticDto updateHabitStatisticDto = getUpdateHabitStatisticDto();

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitStatisticLink + "/{id}", 1)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(habitStatisticService).update(1L, userVO.getId(), updateHabitStatisticDto);
    }

    @Test
    void getTodayStatisticsForAllHabitItemsTest() throws Exception {
        mockMvc.perform(get(habitStatisticLink + "/todayStatisticsForAllHabitItems"))
                .andExpect(status().isOk());
        verify(habitStatisticService).getTodayStatisticsForAllHabitItems("en");
    }

    @Test
    void findAmountOfAcquiredHabitsTest() throws Exception {
        mockMvc.perform(get(habitStatisticLink + "/acquired/count?userId=1"))
                .andExpect(status().isOk());
        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(1L);
    }

    @Test
    void findAmountOfHabitsInProgressTest() throws Exception {
        mockMvc.perform(get(habitStatisticLink + "/in-progress/count?userId=1"))
                .andExpect(status().isOk());
        verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(1L);
    }
}