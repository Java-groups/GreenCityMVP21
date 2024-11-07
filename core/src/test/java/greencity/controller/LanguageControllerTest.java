package greencity.controller;
import greencity.service.LanguageService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LanguageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageController languageController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController).build();
    }

    @Test
    void getAllLanguageCodesTest() throws Exception {

        List<String> mockLanguageCodes = Arrays.asList("en", "uk", "ru");
        when(languageService.findAllLanguageCodes()).thenReturn(mockLanguageCodes);


        mockMvc.perform(get("/language")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/List/item").nodeCount(3))
                .andExpect(xpath("/List/item[1]").string("en"))
                .andExpect(xpath("/List/item[2]").string("uk"))
                .andExpect(xpath("/List/item[3]").string("ru"));

        verify(languageService).findAllLanguageCodes();
    }
}
