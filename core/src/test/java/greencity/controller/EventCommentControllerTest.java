package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventCommentService;
import greencity.service.UserService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventCommentControllerTest {

    private static final String eventsCommentLink = "/events/{eventId}/comments";

    private MockMvc mockMvc;

    @InjectMocks
    private EventCommentController eventCommentController;

    @Mock
    private EventCommentService eventCommentService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    private Principal principal = getPrincipal();

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(eventCommentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void testUpdateCommentSuccess() throws Exception {
        Long eventId = 1L;
        Long commentId = 1L;
        String commentText = "This is a valid comment";

        doNothing().when(eventCommentService).update(eventId, commentId, commentText, principal.getName());

        mockMvc.perform(patch(eventsCommentLink + "/{commentId}", eventId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentText)
                        .principal(principal))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateCommentInvalidText() throws Exception {
        Long eventId = 1L;
        Long commentId = 1L;
        String commentText = "";

        mockMvc.perform(patch(eventsCommentLink + "/{commentId}", eventId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentText)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveTest_ReturnsIsCreated() throws Exception {
        Long eventId = 1L;
        String content = "{\n" +
                "\"text\": \"some comment text\"\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        EventCommentRequestDto request = mapper.readValue(content, EventCommentRequestDto.class);
        EventCommentResponseDto response = EventCommentResponseDto.builder().eventId(eventId).text("some comment text").build();
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(eventCommentService.save(eventId, request, userVO)).thenReturn(response);

        mockMvc.perform(post(eventsCommentLink, eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .principal(principal))
                .andExpect(status().isCreated());
    }

    @Test
    void saveTest_ReturnsBadRequest() throws Exception {
        Long eventId = 1L;
        String content = "{\n" +
                "\"text\": null\n" +
                "}";

        mockMvc.perform(post(eventsCommentLink, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveTest_ReturnsNotFound() throws Exception {
        Long eventId = 0L;
        String content = "{\n" +
                "\"text\": \"some comment text\"\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        EventCommentRequestDto request = mapper.readValue(content, EventCommentRequestDto.class);
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(eventCommentService.save(eventId, request, userVO)).thenThrow(new NotFoundException("Exception"));

        mockMvc.perform(post(eventsCommentLink, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCountOfCommentsTest_ReturnsIsOk() throws Exception {
        Long eventId = 1L;
        when(eventCommentService.countOfComments(eventId)).thenReturn(5);
        mockMvc.perform(get(eventsCommentLink + "/count", eventId)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getCountOfCommentsTest_ReturnsNotFound() throws Exception {
        Long eventId = 0L;
        when(eventCommentService.countOfComments(eventId)).thenThrow(new NotFoundException("Exception"));
        mockMvc.perform(get(eventsCommentLink + "/count", eventId)
                        .principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllEventCommentsTest_ReturnsOk() throws Exception {
        Long eventId = 1L;
        EventCommentResponseDto responseDto = EventCommentResponseDto.builder().eventId(eventId).text("some comment text").build();
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageableDto<EventCommentResponseDto> response = new PageableDto<>(List.of(responseDto), 1, 0, 1);
        when(eventCommentService.getAllEventComments(pageable, eventId)).thenReturn(response);

        mockMvc.perform(get(eventsCommentLink, eventId)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEventCommentsTest_ReturnsBadRequest() throws Exception {
        Long eventId = 1L;

        mockMvc.perform(get(eventsCommentLink + "?sort=unvalid,asc", eventId)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllEventCommentsTest_ReturnsNotFound() throws Exception {
        Long eventId = 1L;
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        when(eventCommentService.getAllEventComments(pageable, eventId)).thenThrow(new NotFoundException("Exception"));

        mockMvc.perform(get(eventsCommentLink, eventId)
                        .principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByEventCommentIdTest_ReturnsOk() throws Exception {
        Long eventId = 1L;
        Long eventCommentId = 1L;
        EventCommentResponseDto response = EventCommentResponseDto.builder().eventId(eventCommentId).text("some comment text").build();
        when(eventCommentService.getByEventCommentId(eventId, eventCommentId)).thenReturn(response);

        mockMvc.perform(get(eventsCommentLink + "/{commentId}", eventId, eventCommentId)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getByEventCommentIdTest_ReturnsNotFound () throws Exception {
        Long eventId = 1L;
        Long eventCommentId = 0L;
        when(eventCommentService.getByEventCommentId(eventId, eventCommentId)).thenThrow(new NotFoundException("Exception"));

        mockMvc.perform(get(eventsCommentLink + "/{commentId}", eventId, eventCommentId)
                        .principal(principal))
                .andExpect(status().isNotFound());
    }
}
