package greencity.service;

import greencity.dto.event.EventResponseDto;
import greencity.dto.event.EventRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    /**
     * Method for updating user event {@link EventResponseDto}.
     *
     * @param requestDto - event update.
     * @param email      - user that edits the event.
     * @param files      - new event images.
     * @return EventResponseDto.
     */
    EventResponseDto update(EventRequestDto requestDto, String email, MultipartFile[] files);
}
