package greencity.controller;

import greencity.entity.EventComment;
import greencity.service.EventCommentService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventCommentControllerTest {
    @Mock
    private EventCommentService eventCommentService;
    @InjectMocks
    private EventCommentController controller;
    private MockMvc mockMvc;
    private static final String EVENT_COMMENT_URL = "/events/comments";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testAddComment() throws Exception {
        String json = """
                {
                  "comment": "string",
                  "parentCommentId": 0
                }
                """;
        mockMvc.perform(post(EVENT_COMMENT_URL + "/{eventId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isCreated());
        verify(eventCommentService, atMostOnce()).save(anyLong(), any(), any());
    }

    @Test
    void updateComment() throws Exception {
        mockMvc.perform(patch(EVENT_COMMENT_URL)
                .param("id", "1")
                .param("text", "Updated comment text")
        ).andExpect(status().isOk());
        verify(eventCommentService, atMostOnce()).save(anyLong(), any(), any());
        verify(eventCommentService, atMostOnce()).update(anyString(), anyLong(), any());
        verify(eventCommentService, atMostOnce()).findById(anyLong());
    }
}
