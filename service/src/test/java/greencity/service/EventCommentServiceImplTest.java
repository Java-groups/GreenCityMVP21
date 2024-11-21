package greencity.service;


import greencity.ModelUtils;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVO;
import static greencity.constant.AppConstant.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCommentServiceImplTest {
    @Mock
    private EventCommentRepo eventCommentRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventCommentServiceImpl eventCommentService;
    @Mock
    private EventService eventService;
    @Mock
    private HttpServletRequest request;


    @Test
    void saveTest() {
        UserVO userVO = getUserVO();
        User user = getUser();
        Event event = ModelUtils.getEvent();
        EventVO eventVO = ModelUtils.getEventVO();
        AddEventCommentDtoRequest addEventCommentDtoRequest = ModelUtils.getEventCommentDtoRequest();
        EventComment eventComment = ModelUtils.getEventComment();
        AddEventCommentDtoResponse addEventCommentDtoResponse = new AddEventCommentDtoResponse();
        when(modelMapper.map(eventRepository.findById(anyLong()), EventVO.class)).thenReturn(eventVO);
        when(modelMapper.map(addEventCommentDtoRequest, EventComment.class)).thenReturn(eventComment);
        when(request.getHeader(AUTHORIZATION)).thenReturn("token");
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(eventVO, Event.class)).thenReturn(event);
        when(eventCommentRepo.save(eventComment)).thenReturn(eventComment);
        when(modelMapper.map(eventComment, AddEventCommentDtoResponse.class)).thenReturn(addEventCommentDtoResponse);
        eventCommentService.save(1L, addEventCommentDtoRequest, userVO);
        verify(eventCommentRepo).save(any(EventComment.class));
    }

    @Test
    void updateTest() {
        EventCommentVO eventCommentVO = ModelUtils.getEventCommentVO();
        EventComment eventComment = ModelUtils.getEventComment();
        when(eventCommentRepo.findById(anyLong())).thenReturn(Optional.of(eventComment));
        when(modelMapper.map(eventComment, EventCommentVO.class)).thenReturn(eventCommentVO);
        when(modelMapper.map(eventCommentVO, EventComment.class)).thenReturn(eventComment);
        eventCommentService.update("Updated comment text", 1L, eventCommentVO.getUser());
        verify(eventCommentRepo).findById(anyLong());
        verify(eventCommentRepo).save(any(EventComment.class));
    }
}
