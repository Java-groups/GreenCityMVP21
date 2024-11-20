package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.dto.event.EventDetailsUpdate;
import greencity.dto.event.EventResponseDto;
import greencity.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    private static final String BASE_LINK = "/events";
    private final Principal principal = getPrincipal();
    private MockMvc mockMvc;
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;
    private EventDetailsUpdate eventRequestDto;
    private EventResponseDto eventResponseDto;

    @BeforeEach
    void setUp() {
        eventRequestDto = ModelUtils.getEventRequestDto();
        eventResponseDto = ModelUtils.getEventResponseDto();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

    }

    @Test
    void update() throws Exception {
        MockMultipartFile jsonFile = getMockMultipartFile();

        Mockito.when(eventService.update(eq(eventRequestDto), eq(principal.getName()), any()))
                .thenReturn(eventResponseDto);

        mockMvc.perform(multipart(BASE_LINK +"/{eventId}", 1L)
                        .file(jsonFile)
                        .principal(principal)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Lectures on garbage segregation"))
                .andExpect(jsonPath("$.dayList[0].id").value(1L))
                .andExpect(jsonPath("$.dayList[0].eventDate").value("2024-12-16"))
                .andExpect(jsonPath("$.dayList[0].eventStartTime").value("09:00:00"))
                .andExpect(jsonPath("$.dayList[0].eventEndTime").value("20:00:00"));

        verify(eventService).update(eq(eventRequestDto), eq(principal.getName()), any());
    }

    private static MockMultipartFile getMockMultipartFile() {
        String jsonRequest = """
                {
                    "id": 1,
                    "title": "Lectures on garbage segregation",
                    "description": "An event focused on promoting environmental awareness and sustainability practices within the community",
                    "eventDays": [
                        {
                            "id": 1,
                            "eventDate": "2024-12-16",
                            "eventStartTime": "09:00:00",
                            "eventEndTime": "20:00:00",
                            "latitude": 47.985,
                            "longitude": -122.559,
                            "isOnline": true,
                            "onlineLink": "https://example.com/event-link"
                        }
                    ],
                    "additionalImages": []
                }
                """;

        return new MockMultipartFile("requestDto", "",
                "application/json", jsonRequest.getBytes(StandardCharsets.UTF_8));
    }
}