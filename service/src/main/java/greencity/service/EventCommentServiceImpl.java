package greencity.service;

import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepo eventCommentRepo;
    private EventRepository eventRepository;
    private ModelMapper modelMapper;
    private HttpServletRequest httpServletRequest;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final EmailService emailService;

    @Override
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest, UserVO user) {
        EventVO eventVO = modelMapper.map(eventRepository.findById(eventId), EventVO.class);
        EventComment eventComment = modelMapper.map(addEventCommentDtoRequest, EventComment.class);
        eventComment.setUser(modelMapper.map(user, User.class));
        eventComment.setEvent(modelMapper.map(eventVO, Event.class));
        if (addEventCommentDtoRequest.getParentCommentId() != 0) {
            EventComment parent = eventCommentRepo.findById(
                            addEventCommentDtoRequest.getParentCommentId())
                    .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
            if (eventComment.getParentComment() == null) {
                eventComment.setParentComment(parent);
            } else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }
        EventComment saved = eventCommentRepo.save(eventComment);
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
                () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, user, accessToken));
        if (!eventVO.getOrganizer().equals(user)) {
            CompletableFuture.runAsync(
                    () -> emailService.sendEventCommentNotification(
                            eventVO.getOrganizer(),
                            modelMapper.map(saved, EventCommentVO.class)
                    )
            );
        }
        return modelMapper.map(eventComment, AddEventCommentDtoResponse.class);
    }

    @Override
    public void update(String newText, long eventId, UserVO user) {
        EventComment eventComment = eventCommentRepo.findById(eventId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION)
        );
        if (!user.getId().equals(eventComment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        eventComment.setComment(newText);
    }
}
