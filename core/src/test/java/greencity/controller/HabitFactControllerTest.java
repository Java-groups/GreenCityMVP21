package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {
    static final String habitFactLink = "/facts";
    MockMvc mockMvc;

    @Mock
    HabitFactServiceImpl habitFactService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    Validator mockValidator;

    @InjectMocks
    HabitFactController habitFactController;

    @Mock
    ObjectMapper objectMapper;

    ErrorAttributes errorAttributes = new DefaultErrorAttributes();

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
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();

        mockMvc.perform(post(habitFactLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(habitFactPostDto)))
                .andExpect(status().isCreated());
        verify(habitFactService).save(habitFactPostDto);
    }

    @Test
    void updateTest() throws Exception {
        HabitFactUpdateDto habitFactUpdateDto = ModelUtils.getHabitFactUpdateDto();

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