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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class SearchControllerTest {
    private static final String SEARCH_CONTROLLER_URL = "/search";
    private static final List<SearchNewsDto> SEARCH_NEWS_DTOS = List.of(
            SearchNewsDto.builder()
                    .id(101L)
                    .title("Title 1")
                    .build(),
            SearchNewsDto.builder()
                    .id(102L)
                    .title("Title 2")
                    .build());

    private MockMvc mockMvc;

    @InjectMocks
    private SearchController searchController;

    @Mock
    private Validator validator;

    @Mock
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void search() throws Exception {
        SearchResponseDto searchResponseDto = SearchResponseDto.builder()
                .ecoNews(SEARCH_NEWS_DTOS)
                .countOfResults(2L)
                .build();

        when(searchService.search(anyString(), anyString())).thenReturn(searchResponseDto);

        mockMvc.perform(get(SEARCH_CONTROLLER_URL)
                        .param("searchQuery", "testQuery")
                        .locale(Locale.ENGLISH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countOfResults").value(2L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ecoNews[0].id").value(101L))
                .andExpect(jsonPath("$.ecoNews[0].title").value("Title 1"))
                .andExpect(jsonPath("$.ecoNews[1].id").value(102L))
                .andExpect(jsonPath("$.ecoNews[1].title").value("Title 2"));

        verify(searchService, times(1)).search("testQuery", Locale.ENGLISH.getLanguage());
    }

    @Test
    void searchEcoNews() throws Exception {
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(SEARCH_NEWS_DTOS, 2L, 0, 2);

        when(searchService.searchAllNews(any(), anyString(), anyString())).thenReturn(pageableDto);

        mockMvc.perform(get(SEARCH_CONTROLLER_URL + "/econews")
                        .param("searchQuery", "testQuery")
                        .param("page", "0")
                        .param("size", "1")
                        .locale(Locale.ENGLISH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(2L))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.page[0].id").value(101L))
                .andExpect(jsonPath("$.page[0].title").value("Title 1"))
                .andExpect(jsonPath("$.page[1].id").value(102L))
                .andExpect(jsonPath("$.page[1].title").value("Title 2"));

        verify(searchService, times(1)).searchAllNews(any(Pageable.class), eq("testQuery"), eq(Locale.ENGLISH.getLanguage()));
    }
}