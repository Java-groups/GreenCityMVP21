package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactTranslationUpdateDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.enums.FactOfDayStatus;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitFactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {
    private static final String habitFactLink = "/facts";
    private MockMvc mockMvc;

    @Mock
    private HabitFactServiceImpl habitFactService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private HabitFactController habitFactController;

    @Mock
    private ObjectMapper objectMapper;

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setValidator(mockValidator)
                .build();
    }

    @Test
    void getRandomFactByHabitIdTest() throws Exception {
        mockMvc.perform(get(habitFactLink + "/random/{habitId}", 1))
                .andExpect(status().isOk());
        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void getHabitFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(habitFactLink + "/dayFact/{languageId}", 1))
                .andExpect(status().isOk());
        verify(habitFactService).getHabitFactOfTheDay(1L);
    }

    @Test
    void getAllTest() throws Exception {
        mockMvc.perform(get(habitFactLink)).andExpect(status().isOk());
        PageRequest pageable = PageRequest.of(0, 20);
        verify(habitFactService).getAllHabitFacts(pageable, "en");
    }

    @Test
    void saveTest() throws Exception {
        HabitFactPostDto habitFactPostDto = HabitFactPostDto.builder()
                .translations(
                        List.of(LanguageTranslationDTO.builder()
                                        .language(LanguageDTO.builder()
                                                .id(1L)
                                                .code("ua")
                                                .build())
                                        .content("ДУУУЖЕ цікавий факт")
                                        .build(),
                                LanguageTranslationDTO.builder()
                                        .language(LanguageDTO.builder()
                                                .id(2L)
                                                .code("en")
                                                .build())
                                        .content("VEEERY interesting fact")
                                        .build(),
                                LanguageTranslationDTO.builder()
                                        .language(LanguageDTO.builder()
                                                .id(3L)
                                                .code("ru")
                                                .build())
                                        .content("ОООЧЕНЬ интересный факт")
                                        .build()
                        ))
                .habit(HabitIdRequestDto.builder()
                        .id(1L)
                        .build())
                .build();

        mockMvc.perform(post(habitFactLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(habitFactPostDto)))
                .andExpect(status().isCreated());
        verify(habitFactService).save(habitFactPostDto);
    }

    @Test
    void updateTest() throws Exception {
        HabitFactUpdateDto habitFactUpdateDto = HabitFactUpdateDto.builder()
                .translations(
                        List.of(HabitFactTranslationUpdateDto.builder()
                                        .factOfDayStatus(FactOfDayStatus.CURRENT)
                                        .language(LanguageDTO.builder()
                                                .id(1L)
                                                .code("ua")
                                                .build())
                                        .content("ДУУУЖЕ цікавий факт")
                                        .build(),
                                HabitFactTranslationUpdateDto.builder()
                                        .factOfDayStatus(FactOfDayStatus.CURRENT)
                                        .language(LanguageDTO.builder()
                                                .id(2L)
                                                .code("en")
                                                .build())
                                        .content("VEEERY interesting fact")
                                        .build(),
                                HabitFactTranslationUpdateDto.builder()
                                        .factOfDayStatus(FactOfDayStatus.CURRENT)
                                        .language(LanguageDTO.builder()
                                                .id(3L)
                                                .code("ru")
                                                .build())
                                        .content("ОООЧЕНЬ интересный факт")
                                        .build()
                        ))
                .habit(HabitIdRequestDto.builder()
                        .id(1L)
                        .build())
                .build();

        mockMvc.perform(put(habitFactLink + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(habitFactUpdateDto)))
                .andExpect(status().isOk());
        verify(habitFactService).update(habitFactUpdateDto, 1L);
    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete(habitFactLink + "/{id}", 1))
                .andExpect(status().isOk());
        verify(habitFactService).delete(1L);
    }

    @Test
    void deleteFailedTest() throws Exception {
        when(habitFactService.delete(1L)).thenThrow(new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID));

        mockMvc.perform(delete(habitFactLink + "/{id}", 1))
                .andExpect(status().isBadRequest());
        verify(habitFactService).delete(1L);
    }
}