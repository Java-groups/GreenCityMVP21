package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventResponseDto;
import greencity.dto.event.EventRequestDto;
import greencity.entity.Event;
import greencity.entity.EventDay;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResponseDto update(EventRequestDto requestDto, String email, MultipartFile[] files) {
        Event eventToUpdate = eventRepo.findById(requestDto.getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);

        if (!organizer.getId().equals(eventToUpdate.getOrganizer().getId())
                && organizer.getRole() != Role.ROLE_ADMIN) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        if (requestDto.getTitle() != null) {
            eventToUpdate.setTitle(requestDto.getTitle());
        }
        if (requestDto.getEventDays() != null) {
            List<EventDay> daysToUpdate = requestDto.getEventDays().stream()
                    .map(eventDayDto -> {
                        EventDay eventDay = modelMapper.map(eventDayDto, EventDay.class);
                        eventDay.setEvent(eventToUpdate);
                        return eventDay;
                    })
                    .toList();
            eventToUpdate.getEventDays().addAll(daysToUpdate);
        }
        if (requestDto.getDescription() != null) {
            eventToUpdate.setDescription(requestDto.getDescription());
        }

        eventRepo.save(eventToUpdate);

        return modelMapper.map(eventToUpdate, EventResponseDto.class);
    }
}
