package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.event.EventDayDto;
import greencity.dto.event.EventDetailsUpdate;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventDay;
import greencity.entity.User;
import greencity.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EventRepository eventRepo;
    @Mock
    private RestClient restClient;
    @InjectMocks
    private EventServiceImpl eventServiceImpl;


    @Test
    void update() {
        Event eventToUpdate = ModelUtils.getEvent();
        EventDetailsUpdate eventDetailsUpdate = ModelUtils.getEventDetailsUpdate();
        EventResponseDto eventResponseDto = ModelUtils.getEventResponseDto();
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();
        EventDay eventDay = ModelUtils.getEventDay();

        when(eventRepo.findById(anyLong())).thenReturn(Optional.of(eventToUpdate));
        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(any(UserVO.class), eq(User.class))).thenReturn(user);
        when(eventRepo.save(any(Event.class))).thenReturn(eventToUpdate);
        when(modelMapper.map(any(EventDayDto.class), eq(EventDay.class)))
                .thenReturn(eventDay);
        when(modelMapper.map(eventToUpdate, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto updated = eventServiceImpl.update(eventDetailsUpdate, user.getEmail(), null);

        assertEquals(eventResponseDto, updated);
        verify(eventRepo).save(eventToUpdate);
        verify(restClient).findByEmail(anyString());
    }
}