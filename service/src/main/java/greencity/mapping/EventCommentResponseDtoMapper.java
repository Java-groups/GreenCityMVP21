package greencity.mapping;

import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventCommentResponseDtoMapper extends AbstractConverter<EventComment, EventCommentResponseDto> {
    private final EventCommentAuthorDtoMapper authorMapper = new EventCommentAuthorDtoMapper();
    private final EventCommentMentionedUserMapper mentionedUserMapper = new EventCommentMentionedUserMapper();
    @Override
    protected EventCommentResponseDto convert(EventComment eventComment) {
        return EventCommentResponseDto.builder()
                .id(eventComment.getId())
                .text(eventComment.getText())
                .createdDate(eventComment.getCreatedDate())
                .author(authorMapper.convert(eventComment.getUser()))
                .eventId(eventComment.getEvent().getId())
                .mentionedUsers(mentionedUserMapper.mapAllToList(eventComment.getMentionedUsers()))
                .build();
    }
}
