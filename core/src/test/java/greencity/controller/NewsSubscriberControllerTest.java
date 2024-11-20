package greencity.controller;

import greencity.ModelUtils;
import greencity.service.EmailService;
import greencity.service.SubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NewsSubscriberControllerTest {
    @Mock
    private SubscriberService subscriberService;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private NewsSubscriberController newsSubscriberController;
    private MockMvc mockMvc;
    private final String ENDPOINT = "/newsSubscriber";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(newsSubscriberController).build();
    }

    @Test
    void addSubscriberTest() throws Exception {
        String json = """
                {
                "email": "testEmail@gmail.com"
                }
                """;
        mockMvc.perform(post(ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(subscriberService, atMostOnce()).save(any());
        verify(emailService, atMostOnce()).sendConfirmationLetter(any());
    }

    @Test
    void unsubscribeTest() throws Exception {
        String UUID = ModelUtils.getNewsSubscriberVO().getUnsubscribeToken();
        mockMvc.perform(get(ENDPOINT + "/unsubscribe")
                .param("email", "testEmail@gmail.com")
                .param("unsubscribeToken", UUID))
                .andExpect(status().isOk());
        verify(subscriberService, atMostOnce()).unsubscribe(anyString(), any());
    }
    @Test
    void sendNewsTest() throws Exception {
        String json = """
                {
                  "creationDate": "2024-11-10T13:49:45.078Z",
                  "imagePath": "string",
                  "id": 1,
                  "title": "string",
                  "content": "string",
                  "shortInfo": "string",
                  "author": {
                    "id": 0,
                    "name": "string"
                  },
                  "tags": [
                    "string"
                  ],
                  "tagsUa": [
                    "string"
                  ],
                  "likes": 0,
                  "dislikes": 0,
                  "countComments": 0
                }
                """;
        mockMvc.perform(post(ENDPOINT + "/sendNews")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(emailService, atMostOnce()).sendNewNewsForSubscriber(anyList(), any());
    }
}
