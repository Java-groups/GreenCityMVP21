package greencity.service;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventImagesRepository;
import greencity.repository.EventRepository;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EventServiceImplTest {
    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepo;

    @Mock
    private EventImagesRepository eventImagesRepo;

    @Mock
    private UserRepo userRepo;

    private Event event;
    private User organizer;
    private User admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        organizer = new User();
        organizer.setId(1L);
        organizer.setRole(Role.ROLE_USER);

        admin = new User();
        admin.setId(2L);
        admin.setRole(Role.ROLE_ADMIN);

        event = new Event();
        event.setId(100L);
        event.setOrganizer(organizer);
    }

    @Test
    void deleteEvent_Success_AsOrganizer() {
        when(eventRepo.findById(100L)).thenReturn(java.util.Optional.of(event));
        when(userRepo.findById(1L)).thenReturn(java.util.Optional.of(organizer));

        eventService.deleteEvent(100L, 1L);

        verify(eventRepo, times(1)).delete(event);
        verify(eventImagesRepo, times(1)).deleteEventImagesByEvent_Id(100L);
        verify(eventRepo, times(1)).deleteEventDayByEventId(100L);
    }

    @Test
    void deleteEvent_Success_AsAdmin() {
        when(eventRepo.findById(100L)).thenReturn(java.util.Optional.of(event));
        when(userRepo.findById(2L)).thenReturn(java.util.Optional.of(admin));

        eventService.deleteEvent(100L, 2L);

        verify(eventRepo, times(1)).delete(event);
        verify(eventImagesRepo, times(1)).deleteEventImagesByEvent_Id(100L);
        verify(eventRepo, times(1)).deleteEventDayByEventId(100L);
    }

    @Test
    void deleteEvent_Failure_NoPermission() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(3L);
        unauthorizedUser.setRole(Role.ROLE_USER);

        when(eventRepo.findById(100L)).thenReturn(java.util.Optional.of(event));
        when(userRepo.findById(3L)).thenReturn(java.util.Optional.of(unauthorizedUser));

        assertThrows(UserHasNoPermissionToAccessException.class, () -> eventService.deleteEvent(100L, 3L));
    }

    @Test
    void deleteEvent_Failure_EventNotFound() {
        when(eventRepo.findById(100L)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.deleteEvent(100L, 1L));
    }
}

