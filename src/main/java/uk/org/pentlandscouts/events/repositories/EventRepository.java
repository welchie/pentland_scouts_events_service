package uk.org.pentlandscouts.events.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventId;

import java.util.List;

@EnableScan
public interface EventRepository extends CrudRepository<Event, EventId> {

    List<Event> findAll();
    List<Event> findByNameAndVenue(String name, String venue);

    List<Event> findByUid(String uid);

}
