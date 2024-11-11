package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventDayDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.event.EventRequestDto;
import greencity.entity.Event;
import greencity.entity.EventDay;
import greencity.entity.EventImages;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventImagesRepository;
import greencity.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepo;
    private final EventImagesRepository eventImagesRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final FileService fileService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
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
                    .collect(Collectors.toList());

            List<EventDay> toRemove = eventToUpdate.getEventDays().stream()
                    .filter(day -> daysToUpdate.stream()
                            .noneMatch(newDay -> newDay.getId().equals(day.getId())))
                    .collect(Collectors.toList());
            toRemove.forEach(day -> eventRepo.deleteEventDayByEventId(day.getId()));

            eventToUpdate.setEventDays(daysToUpdate);
        }
        if (requestDto.getDescription() != null) {
            eventToUpdate.setDescription(requestDto.getDescription());
        }



        Event saved = eventRepo.save(eventToUpdate);
        return modelMapper.map(saved, EventResponseDto.class);
    }
}
