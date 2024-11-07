package greencity.repository;

import greencity.entity.NewsSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepo extends JpaRepository<NewsSubscriber, Long> {
    Optional<NewsSubscriber> findByEmail(String email);

    @Query(value = "SELECT ns FROM NewsSubscriber ns WHERE ns.email = ?1 and ns.unsubscribeToken = ?2")
    Optional<NewsSubscriber> findByEmailAndUnsubscribeToken(String email, String unsubscribeToken);
}
