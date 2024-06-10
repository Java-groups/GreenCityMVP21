package greencity.service;

import greencity.client.RestClient;
import greencity.dto.user.UserVO;
import greencity.constant.ErrorMessage;
import greencity.entity.Event;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final RestClient restClient;

    /**
     * Deletes an event with the specified eventId and name.
     *
     * @param eventId The ID of the event to be deleted.
     * @param email The email of the user who is deleting the event.
     */
    @Override
    public void delete(Long eventId, String email) {
        UserVO userVO = restClient.findByEmail(email);
        Event toDelete = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        if (toDelete.getOrganizer().getId().equals(userVO.getId()) || userVO.getRole() == Role.ROLE_ADMIN) {
            eventRepo.delete(toDelete);
        } else {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
    }
}
