package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventDetailsUpdate;
import greencity.dto.event.EventResponseDto;
import greencity.dto.event.EventVO;
import greencity.entity.*;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventImagesRepository;
import greencity.repository.EventRepository;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private final TagsService tagsService;
    private final UserRepo userRepo;
//    private final NotificationService notificationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EventResponseDto update(EventDetailsUpdate requestDto, String email, MultipartFile[] files) {
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

    private void updateEvent(EventDetailsUpdate requestDto, Event eventToUpdate) {
        if (requestDto.getTitle() != null) {
            eventToUpdate.setTitle(requestDto.getTitle());
        }

        if (requestDto.getDescription() != null) {
            eventToUpdate.setDescription(requestDto.getDescription());
        }

        if (requestDto.getImage() == null || requestDto.getImage().isEmpty()) {
            fileService.delete(eventToUpdate.getImage());
        }

        if (requestDto.getTags() != null) {
            eventToUpdate.setTags(modelMapper.map(tagsService.findTagsWithAllTranslationsByNamesAndType(
                    requestDto.getTags(), TagType.EVENT), new TypeToken<List<Tag>>() {
            }.getType()));
        }
    }

    private void updateEventDay(EventDetailsUpdate requestDto, Event eventToUpdate) {
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
                    .toList();
            toRemove.forEach(day -> eventRepo.deleteEventDayByEventId(day.getId()));

            eventToUpdate.setEventDays(daysToUpdate);
        }
    }

    private void updateAdditionalImages(EventDetailsUpdate requestDto, MultipartFile[] files, Event eventToUpdate) {
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
        uploadFilesAndCreatingLinks(requestDto, files, eventToUpdate);
    }

    private void uploadFilesAndCreatingLinks(EventDetailsUpdate requestDto, MultipartFile[] files, Event eventToUpdate) {
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

    @Override
    @Transactional
    public void deleteEvent(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with ID: " + eventId));
        if (!event.getOrganizer().getId().equals(userId) && !isAdmin(userId)) {
            throw new UserHasNoPermissionToAccessException("You do not have permission to delete this event.");
        }

        eventImagesRepo.deleteEventImagesByEvent_Id(eventId);
        eventRepo.deleteEventDayByEventId(eventId);

//        notificationSrvice.notifyAttendees(event.getAttendants(), "The event has been deleted");
        eventRepo.delete(event);
    }

    @Override
    public EventVO findById(long eventId) {
        return modelMapper.map(eventRepo.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found by this id")), EventVO.class);
    }

    private boolean isAdmin(Long userId) {
        return userRepo.findById(userId)
                       .map(User::getRole)
                       .orElse(Role.ROLE_USER) == Role.ROLE_ADMIN;
    }
}
