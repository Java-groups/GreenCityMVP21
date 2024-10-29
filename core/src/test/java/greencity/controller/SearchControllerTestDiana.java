package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTestDiana {

    private MockMvc mockMvc;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @BeforeEach
    void setUp() {
        Validator mockValidator = mock(Validator.class);

        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setValidator(mockValidator)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void searchEverythingTest() throws Exception {

        String searchQuery = "Title";
        SearchResponseDto mockResponse = SearchResponseDto.builder()
                .ecoNews(List.of(new SearchNewsDto()))
                .countOfResults(1L)
                .build();

        Mockito.when(searchService.search(eq(searchQuery), eq(Locale.ENGLISH.getLanguage())))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/search")
                        .param("searchQuery", searchQuery)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ecoNews", hasSize(1)))
                .andExpect(jsonPath("$.countOfResults").value(1));

        verify(searchService).search(eq(searchQuery), eq(Locale.ENGLISH.getLanguage()));
    }

    @Test
    void searchEcoNewsTest() throws Exception {

        String searchQuery = "Eco news title";
        Pageable pageable = PageRequest.of(0, 5);
        PageableDto<SearchNewsDto> mockResponse = new PageableDto<>(
                List.of(new SearchNewsDto()), 1L, 0, 1);

        Mockito.when(searchService.searchAllNews(eq(pageable), eq(searchQuery), eq(Locale.ENGLISH.getLanguage())))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/search/econews")
                        .param("searchQuery", searchQuery)
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(searchService).searchAllNews(eq(pageable), eq(searchQuery), eq(Locale.ENGLISH.getLanguage()));
    }
}
