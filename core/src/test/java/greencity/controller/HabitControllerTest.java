package greencity.controller;

import greencity.config.SecurityConfig;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
public class HabitControllerTest {
    MockMvc mockMvc;
    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private HabitController habitController;

    @Mock
    private UserVO userVO;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HabitController(habitService, tagsService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Get habit by ID in specified language")
    void testGetHabitById() throws Exception {
        Long habitId = 1L;
        String language = "en";
        HabitDto habitDto = new HabitDto();
        habitDto.setId(habitId);

        when(habitService.getByIdAndLanguageCode(habitId, language)).thenReturn(habitDto);

        mockMvc.perform(get("/habit/{id}", habitId)
                        .header("Accept-Language", language)
                        .header("Accept", "application/json")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(habitId));

        verify(habitService, times(1)).getByIdAndLanguageCode(habitId, language);
    }

    @Test
    @DisplayName("Get all habits")
    void testGetAll() {
        Pageable pageable = Pageable.unpaged();
        Locale locale = Locale.ENGLISH;

        List<HabitDto> habitDtos = new ArrayList<>();
        habitDtos.add(new HabitDto());
        long totalElements = 1;
        int totalPages = 1;
        int size = 1;

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habitDtos, totalElements, totalPages, size);

        when(habitService.getAllHabitsByLanguageCode(userVO, pageable, locale.getLanguage()))
                .thenReturn(pageableDto);

        ResponseEntity<PageableDto<HabitDto>> response = habitController.getAll(userVO, locale, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pageableDto, response.getBody());
        verify(habitService, times(1)).getAllHabitsByLanguageCode(userVO, pageable, locale.getLanguage());
    }
}
