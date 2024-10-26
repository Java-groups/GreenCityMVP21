package greencity.controller;

import greencity.service.HabitFactService;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class HabitFactControllerPDTest {
    private MockMvc mockMvc;

    @Mock
    private HabitFactService habitFactService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private HabitFactController habitFactController;

    private final Locale locale = new Locale("en");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(validator)
            .build();
    }

    @Test
    public void getRandomFactByHabitIdTest() throws Exception {
        mockMvc.perform(get("/facts/random/1")).andExpect(status().isOk());
        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(1L, locale.getLanguage());
    }

    @Test
    public void getHabitFactOfTheDayTest() throws Exception {
        mockMvc.perform(get("/facts/dayFact/100")).andExpect(status().isOk());
        verify(habitFactService).getHabitFactOfTheDay(100L);
    }

    @Test
    public void getAllTest() throws Exception {
        mockMvc.perform(get("/facts")).andExpect(status().isOk());
        verify(habitFactService).getAllHabitFacts(any(), eq(locale.getLanguage()));
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/facts/100")).andExpect(status().isOk());
        verify(habitFactService).delete(100L);
    }

    @Test
    public void saveTest() throws Exception {
        String content = """
            {
              "translations": [
                {
                  "language": {
                    "id": 1,
                    "code": "en"
                  },
                  "content": "Hello!"
                }
              ],
              "habit": {
                "id": 1
              }
            }
            """;
        mockMvc.perform(
            post("/facts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isCreated());
        verify(habitFactService).save(any());
    }

    @Test
    public void updateTest() throws Exception {
        String content = """
            {
              "translations": [
                {
                  "language": {
                    "id": 1,
                    "code": "en"
                  },
                  "content": "Hello2!"
                }
              ],
              "habit": {
                "id": 1
              }
            }
            """;

        mockMvc.perform(
            put("/facts/100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isOk());
        verify(habitFactService).update(any(), eq(100L));
    }

    @Test
    public void deleteFailedTest() throws Exception {
        when(habitFactService.delete(anyLong())).thenThrow(
            new IllegalArgumentException("Invalid ID"));
        mockMvc.perform(delete("/facts/1")).andExpect(status().isBadRequest());
    }
}
