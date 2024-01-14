package uk.org.pentlandscouts.events.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.EventAttendeeId;

import java.util.List;

@EnableScan
public interface EventAttendeeRepository extends CrudRepository<EventAttendee, EventAttendeeId> {

    List<EventAttendee> findAll();

    List<EventAttendee> findByUid(String uid);
    List<EventAttendee> findByEventUid(String eventUid);

    List<EventAttendee> findByPersonUid(String personUid);

    List<EventAttendee> findByEventUidAndPersonUid(String eventUid,String personUid);

    //List<EventAttendee> findByEventUidAndCheckedIn(String eventUid, Boolean checkedIn);


}
