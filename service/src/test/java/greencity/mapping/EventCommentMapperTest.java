package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.entity.EventComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class EventCommentMapperTest {
    @InjectMocks
    EventCommentMapper mapper;

    @Test
    void convert() {
        EventCommentRequestDto requestDto = ModelUtils.getEventCommentRequestDto();
        EventComment expected = EventComment.builder().text(requestDto.getText()).build();
        assertEquals(expected, mapper.convert(requestDto));
    }
}