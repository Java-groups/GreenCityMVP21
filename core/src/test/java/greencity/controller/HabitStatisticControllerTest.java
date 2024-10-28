package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.GetHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitStatisticService;
import greencity.service.LanguageService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HabitStatisticControllerTest {
    private static final String HabitStatisticLink = "/habit/statistic";
    private MockMvc mockMvc;
    @Mock
    private HabitStatisticService habitStatisticService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private LanguageService languageService;
    @InjectMocks
    private HabitStatisticController habitStatisticController;
    private ObjectMapper objectMapper;
    private Principal principal;
    private UserVO userVO;

    @BeforeEach
    void setUp() {
        userVO = getUserVO();
        principal = getPrincipal();
        ErrorAttributes errorAttributes = new DefaultErrorAttributes();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);
    }

    @Test
    void findAllByHabitId() throws Exception {
        Long habitId = 1L;
        GetHabitStatisticDto habitStatisticDto = GetHabitStatisticDto.builder()
                .amountOfUsersAcquired(5L)
                .habitStatisticDtoList(List.of(
                        HabitStatisticDto.builder().id(1L).build(),
                        HabitStatisticDto.builder().id(2L).build()
                ))
                .build();
        when(habitStatisticService.findAllStatsByHabitId(habitId)).thenReturn(habitStatisticDto);

        mockMvc.perform(get(HabitStatisticLink + "/{habitId}", habitId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amountOfUsersAcquired").value(5))
                .andExpect(jsonPath("$.habitStatisticDtoList[0].id").value(1))
                .andExpect(jsonPath("$.habitStatisticDtoList[1].id").value(2));

        verify(habitStatisticService).findAllStatsByHabitId(habitId);
    }

    @Test
    void findAllStatsByHabitAssignId() throws Exception {
        Long habitAssignId = 1L;
        List<HabitStatisticDto> list = List.of(
                HabitStatisticDto.builder()
                .id(1L)
                .build(),
                HabitStatisticDto.builder()
                .id(2L)
                .build());

        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId)).thenReturn(list);
        mockMvc.perform(get(HabitStatisticLink + "/assign/{habitAssignId}", habitAssignId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(habitStatisticService).findAllStatsByHabitAssignId(habitAssignId);
    }

    @Test
    void saveHabitStatistic_successful() throws Exception {
        Long habitId = 1L;
        String json = "{\n" +
                "\"amountOfItems\": 10,\n" +
                "\"habitRate\": \"GOOD\",\n" +
                "\"createDate\": \"2024-10-24T17:21:02Z\"\n" +
                "}";

        mockMvc.perform(
                post(HabitStatisticLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .principal(principal))
                .andExpect(status().isCreated());

        AddHabitStatisticDto addHabitStatisticDto = objectMapper.readValue(json, AddHabitStatisticDto.class);

        verify(habitStatisticService).saveByHabitIdAndUserId(eq(habitId), eq(userVO.getId()), eq(addHabitStatisticDto));
    }

    @Test
    void saveHabitStatistic_InvalidInput() throws Exception {
        Long habitId = 1L;
        String invalidJson = "{\n" +
                "\"amountOfItems\": -10,\n" +
                "\"habitRate\": \"INVALID_RATE\",\n" +
                "\"createDate\": \"2024-10-24T17:21:02Z\"\n" +
                "}";

        mockMvc.perform(post(HabitStatisticLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(invalidJson)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    void saveHabitStatistic_HabitNotFound() throws Exception {
        Long habitId = 999L;
        String json = "{\n" +
                "\"amountOfItems\": 10,\n" +
                "\"habitRate\": \"GOOD\",\n" +
                "\"createDate\": \"2024-10-24T17:21:02Z\"\n" +
                "}";

        when(habitStatisticService.saveByHabitIdAndUserId(eq(habitId), anyLong(), any(AddHabitStatisticDto.class)))
                .thenThrow(new NotFoundException("Habit not found"));

        mockMvc.perform(post(HabitStatisticLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .principal(principal))
                .andExpect(status().isNotFound());

        verify(habitStatisticService).saveByHabitIdAndUserId(eq(habitId), anyLong(), any(AddHabitStatisticDto.class));
    }

    @Test
    void updateStatistic() throws Exception {
        Long id = 1L;
        String json = "{\n" +
                "\"amountOfItems\": 5,\n" +
                "\"habitRate\": \"DEFAULT\"\n" +
                "}";

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);
        mockMvc.perform(put(HabitStatisticLink + "/{id}", id)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isOk());

        UpdateHabitStatisticDto updateHabit = objectMapper.readValue(json, UpdateHabitStatisticDto.class);

        verify(habitStatisticService).update(eq(id), eq(userVO.getId()), eq(updateHabit));
    }

    @Test
    void getTodayStatisticsForAllHabitItems_validLocale_shouldReturnOk() throws Exception {
        Locale validLocale = new Locale("en");
        List<HabitItemsAmountStatisticDto> statistics = Arrays.asList(
                new HabitItemsAmountStatisticDto("habitItem1", 5),
                new HabitItemsAmountStatisticDto("habitItem2", 3)
        );

        when(languageService.findAllLanguageCodes()).thenReturn(Arrays.asList("en", "ua"));
        when(habitStatisticService.getTodayStatisticsForAllHabitItems(validLocale.getLanguage())).thenReturn(statistics);

        mockMvc.perform(get(HabitStatisticLink + "/todayStatisticsForAllHabitItems")
                        .locale(validLocale)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].habitItem").value("habitItem1"))
                .andExpect(jsonPath("$[0].notTakenItems").value(5))
                .andExpect(jsonPath("$[1].habitItem").value("habitItem2"))
                .andExpect(jsonPath("$[1].notTakenItems").value(3));

        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(validLocale.getLanguage());
    }

    @Test
    void findAmountOfAcquiredHabits() throws Exception {
        Long userId = 1L;
        Long habitsReturnedByUserId = 5L;
        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId))
                .thenReturn(habitsReturnedByUserId);

        mockMvc.perform(get(HabitStatisticLink + "/acquired/count", userId)
                .param("userId", String.valueOf(userId))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));


        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(userId);
    }

    @Test
    void findAmountOfHabitsInProgress() throws Exception {
        Long userId = 1L;
        Long habitCount = 5L;

        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(userId)).thenReturn(habitCount);

        this.mockMvc.perform(get(HabitStatisticLink + "/in-progress/count", userId)
                        .param("userId", String.valueOf(userId))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(userId);
    }
}