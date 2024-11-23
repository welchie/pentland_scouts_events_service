package uk.org.pentlandscouts.events.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.model.EventAttendeeId;

import java.util.List;

@EnableScan
public interface EventAttendeeHistRepository extends CrudRepository<EventAttendeeHist, EventAttendeeId> {

    List<EventAttendeeHist> findAll();

    List<EventAttendeeHist> findByUid(String uid);
    List<EventAttendeeHist> findByEventUid(String eventUid);

    List<EventAttendeeHist> findByPersonUid(String personUid);

    List<EventAttendeeHist> findByEventUidAndPersonUid(String eventUid,String personUid);

    List<EventAttendeeHist> findByHistDateBetween(String startDate,String endDate);


}
