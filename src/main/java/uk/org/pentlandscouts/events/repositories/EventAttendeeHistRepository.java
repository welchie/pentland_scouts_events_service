package uk.org.pentlandscouts.events.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.model.EventAttendeeId;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Repository
public class EventAttendeeHistRepository {

    @Autowired
    private DynamoDbTemplate dynamoDbTemplate;

    public EventAttendeeHist save(EventAttendeeHist attendee) {
        dynamoDbTemplate.save(attendee);
        return attendee;
    }

    public <S extends EventAttendeeHist> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    public Optional<EventAttendeeHist> findById(EventAttendeeId id) {
        if (id == null || id.getUid() == null || id.getSortKey() == null) {
            return Optional.empty();
        }
        EventAttendeeHist attendee = dynamoDbTemplate.load(
                Key.builder()
                        .partitionValue(id.getUid())
                        .sortValue(id.getSortKey())
                        .build(),
                EventAttendeeHist.class
        );
        return Optional.ofNullable(attendee);
    }

    public boolean existsById(EventAttendeeId id) {
        return findById(id).isPresent();
    }

    public List<EventAttendeeHist> findAll() {
        return dynamoDbTemplate.scanAll(EventAttendeeHist.class).items().stream().toList();
    }

    public Iterable<EventAttendeeHist> findAllById(Iterable<EventAttendeeId> ids) {
        java.util.List<EventAttendeeHist> list = new java.util.ArrayList<>();
        for (EventAttendeeId id : ids) {
            findById(id).ifPresent(list::add);
        }
        return list;
    }

    public long count() {
        return findAll().size();
    }

    public void deleteById(EventAttendeeId id) {
        findById(id).ifPresent(this::delete);
    }

    public void delete(EventAttendeeHist attendee) {
        dynamoDbTemplate.delete(attendee);
    }

    public void deleteAllById(Iterable<? extends EventAttendeeId> ids) {
        for (EventAttendeeId id : ids) {
            deleteById(id);
        }
    }

    public void deleteAll(Iterable<? extends EventAttendeeHist> entities) {
        for (EventAttendeeHist entity : entities) {
            delete(entity);
        }
    }

    public void deleteAll() {
        deleteAll(findAll());
    }

    public List<EventAttendeeHist> findByUid(String uid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder().partitionValue(uid).build()
                        ))
                        .build(),
                EventAttendeeHist.class
        ).items().stream().toList();
    }

    public List<EventAttendeeHist> findByEventUid(String eventUid) {
        return findAll().stream()
                .filter(ea -> Objects.equals(ea.getEventUid(), eventUid))
                .toList();
    }

    public List<EventAttendeeHist> findByPersonUid(String personUid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder().partitionValue(personUid).build()
                        ))
                        .build(),
                EventAttendeeHist.class,
                "event-person-index"
        ).items().stream().toList();
    }

    public List<EventAttendeeHist> findByEventUidAndPersonUid(String eventUid, String personUid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder()
                                        .partitionValue(personUid)
                                        .sortValue(eventUid)
                                        .build()
                        ))
                        .build(),
                EventAttendeeHist.class,
                "event-person-index"
        ).items().stream().toList();
    }

    public List<EventAttendeeHist> findByHistDateBetween(String startDate, String endDate) {
        return findAll().stream()
                .filter(eah -> isDateBetween(eah.getHistDate(), startDate, endDate))
                .toList();
    }

    private boolean isDateBetween(String dateStr, String startStr, String endStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = sdf.parse(dateStr);
            java.util.Date start = sdf.parse(startStr);
            java.util.Date end = sdf.parse(endStr);
            return !date.before(start) && !date.after(end);
        } catch (Exception e) {
            return dateStr.compareTo(startStr) >= 0 && dateStr.compareTo(endStr) <= 0;
        }
    }
}
