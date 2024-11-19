package greencity.repository;

import greencity.entity.EventImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventImagesRepository extends JpaRepository<EventImages, Long> {

    void deleteEventImagesByEvent_Id(Long id);
}
