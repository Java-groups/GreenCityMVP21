package greencity.repository;

import greencity.dto.event.model.EventModelDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

//@Repository
@Component //need to be removed
public class EventRepo {

    private List<EventModelDto> events = new ArrayList<>();
    private AtomicLong eventId = new AtomicLong();

    public EventModelDto save(EventModelDto event) {
        event.setId(eventId.incrementAndGet());
        events.add(event);
        return event;
    }

    public List<EventModelDto> findAll(Pageable pageable) {
        return events;
    }

    public EventModelDto findById(Long id) {
        return events.stream().filter(e -> e.getId().equals(id)).toList().get(0);
    }

    public List<EventModelDto> findAllByAuthorId(Pageable pageable, Long userId) {
        return events.stream().filter(e -> e.getAuthor().getId().equals(userId)).toList();
    }
}
