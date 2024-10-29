package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.entity.Habit;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class HabitFactControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitFactService habitFactService;

    @InjectMocks
    HabitFactController habitFactController;

    @Mock
    ModelMapper modelMapper;

    @Mock
    Validator validator;

    private static final String habbitFactLink = "/facts";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void getRandomFactByHabitIdSuccessWithStatusOk() throws Exception {
        Long habitId = 1L;
        String language = "en";
        LanguageTranslationDTO mockTranslationDTO = new LanguageTranslationDTO();
        mockTranslationDTO.setContent("This is a random fact");
        mockTranslationDTO.setLanguage(new LanguageDTO(1L, language));
        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(habitId, language))
                .thenReturn(mockTranslationDTO);
        mockMvc.perform(get(habbitFactLink + "/random/{habitId}", habitId)
                        .header("Accept-Language", language)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content")
                        .value("This is a random fact"));

        verify(habitFactService, times(1))
                .getRandomHabitFactByHabitIdAndLanguage(habitId, language);
    }


    @Test
    void getHabitFactOfTheDaySuccessWithStatusOk() throws Exception {
        Long languageId = 1L;
        LanguageTranslationDTO mockTranslationDTO = new LanguageTranslationDTO();
        mockTranslationDTO.setContent("random fact");
        mockTranslationDTO.setLanguage(new LanguageDTO(1L, "en"));

        when(habitFactService.getHabitFactOfTheDay(languageId))
                .thenReturn(mockTranslationDTO);
        mockMvc.perform(get(habbitFactLink + "/dayFact/{languageId}", languageId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language.id").value(1))
                .andExpect(jsonPath("$.language.code").value("en"))
                .andExpect(jsonPath("$.content").value("random fact"));

        verify(habitFactService, times(1)).getHabitFactOfTheDay(languageId);

    }

    @Test
    void getAllSuccessWithStatusOk() throws Exception {
        Pageable page = PageRequest.of(0, 2);
        LanguageTranslationDTO langTranslDTOMock1 = new LanguageTranslationDTO();
        langTranslDTOMock1.setLanguage(new LanguageDTO(1L, "en"));
        langTranslDTOMock1.setContent("Fact 1");

        LanguageTranslationDTO langTranslDTOMock2 = new LanguageTranslationDTO();
        langTranslDTOMock2.setLanguage(new LanguageDTO(2L, "en"));
        langTranslDTOMock2.setContent("Fact 2");

        List<LanguageTranslationDTO> mockFacts = List.of(langTranslDTOMock1, langTranslDTOMock2);
        PageableDto<LanguageTranslationDTO> mockPageableDto = new PageableDto<>(mockFacts, 2L, 0, 1);


        when(habitFactService.getAllHabitFacts(page, "en"))
                .thenReturn(mockPageableDto);
        mockMvc.perform(get(habbitFactLink)
                        .param("lang", "en")
                        .param("page", "0")
                        .param("size", "2")
                        .header("accept", "*/*")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page[0].language.id").value(1))
                .andExpect(jsonPath("$.page[0].language.code").value("en"))
                .andExpect(jsonPath("$.page[0].content").value("Fact 1"))
                .andExpect(jsonPath("$.page[1].language.id").value(2))
                .andExpect(jsonPath("$.page[1].language.code").value("en"))
                .andExpect(jsonPath("$.page[1].content").value("Fact 2"));

        verify(habitFactService, times(1)).getAllHabitFacts(page, "en");
    }

    @Test
    void saveHabitFactDTOAndGetStatusCreated() throws Exception {
        HabitIdRequestDto habitIdRequestDto = new HabitIdRequestDto();
        habitIdRequestDto.setId(1L);
        HabitFactPostDto habitFactPostDto = new HabitFactPostDto();
        habitFactPostDto.setHabit(habitIdRequestDto);
        habitFactPostDto.setTranslations(new ArrayList<>());
        HabitVO habitVO = new HabitVO();
        habitVO.setId(1L);
        habitVO.setComplexity(1);
        habitVO.setImage("img");
        HabitFactVO habitFactVO = HabitFactVO.builder().build()
                .setId(1L)
                .setTranslations(new ArrayList<>())
                .setHabit(habitVO);

        when(habitFactService.save(habitFactPostDto)).thenReturn(habitFactVO);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(habitFactPostDto);
        mockMvc.perform(post(habbitFactLink)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonValue))
                .andExpect(status().isCreated());

        verify(habitFactService, times(1)).save(habitFactPostDto);
    }

    @Test
    void updateHabitFactSuccessAndGetStatusOk() throws Exception {
        HabitFactUpdateDto habitFactUpdateDto = HabitFactUpdateDto.builder()
                .habit(HabitIdRequestDto.builder().id(1L).build())
                .translations(new ArrayList<>())
                .build();
        Habit habit = Habit.builder()
                .id(1L)
                .isCustomHabit(false)
                .image("img")
                .complexity(1)
                .defaultDuration(1)
                .build();
        HabitVO habitVO = modelMapper.map(habit, HabitVO.class);
        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(1L)
                .translations(new ArrayList<>())
                .habit(habitVO).build();
        when(habitFactService.update(habitFactUpdateDto, 1L)).thenReturn(habitFactVO);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(habitFactUpdateDto);

        mockMvc.perform(put(habbitFactLink + "/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonValue))
                .andExpect(status().isOk());
        verify(habitFactService, times(1)).update(habitFactUpdateDto, 1L);
    }

    @Test
    void deleteSuccessAndGetOk() throws Exception {
        when(habitFactService.delete(1L)).thenReturn(1L);
        mockMvc.perform(delete(habbitFactLink + "/{id}", 1L)).andExpect(status().isOk());
        verify(habitFactService, times(1)).delete(1L);
    }


}
