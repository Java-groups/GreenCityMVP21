package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        updateEvent(requestDto, eventToUpdate);
        updateEventDay(requestDto, eventToUpdate);
        updateAdditionalImages(requestDto, files, eventToUpdate);

        Event saved = eventRepo.save(eventToUpdate);
        return modelMapper.map(saved, EventResponseDto.class);
    }

    private void updateAdditionalImages(EventRequestDto requestDto, MultipartFile[] files, Event eventToUpdate) {
        Set<String> requestLinks = new HashSet<>(requestDto.getAdditionalImages());
        List<EventImages> imagesFromDb = eventToUpdate.getAdditionalImages();

        List<EventImages> imagesToDelete = imagesFromDb.stream()
                .filter(image -> !requestLinks.contains(image.getLink()))
                .collect(Collectors.toList());
        imagesToDelete.forEach(image -> fileService.delete(image.getLink()));

        if (!imagesToDelete.isEmpty()) {
            eventImagesRepo.deleteAll(imagesToDelete);
        }

        List<EventImages> imagesToKeep = imagesFromDb.stream()
                .filter(image -> requestLinks.contains(image.getLink()))
                .collect(Collectors.toList());

        eventToUpdate.setAdditionalImages(imagesToKeep);

        if (files != null) {
            for (MultipartFile file : files) {
                String link = fileService.upload(file);

                if (requestDto.getImage() == null) {
                    eventToUpdate.setImage(link);
                } else {
                    eventToUpdate.getAdditionalImages().add(EventImages.builder()
                            .link(link)
                            .event(eventToUpdate)
                            .build());
                }
            }
        }
    }

    private void updateEvent(EventRequestDto requestDto, Event eventToUpdate) {
        if (requestDto.getTitle() != null) {
            eventToUpdate.setTitle(requestDto.getTitle());
        }

        if (requestDto.getDescription() != null) {
            eventToUpdate.setDescription(requestDto.getDescription());
        }

        if (requestDto.getImage() == null || requestDto.getImage().isEmpty()) {
            fileService.delete(eventToUpdate.getImage());
        }
    }

    private void updateEventDay(EventRequestDto requestDto, Event eventToUpdate) {
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
    }
}
