package uk.org.pentlandscouts.events.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.EventAttendeeId;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Repository
public class EventAttendeeRepository {

    @Autowired
    private DynamoDbTemplate dynamoDbTemplate;

    public EventAttendee save(EventAttendee attendee) {
        dynamoDbTemplate.save(attendee);
        return attendee;
    }

    public <S extends EventAttendee> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    public Optional<EventAttendee> findById(EventAttendeeId id) {
        if (id == null || id.getUid() == null || id.getSortKey() == null) {
            return Optional.empty();
        }
        EventAttendee attendee = dynamoDbTemplate.load(
                Key.builder()
                        .partitionValue(id.getUid())
                        .sortValue(id.getSortKey())
                        .build(),
                EventAttendee.class
        );
        return Optional.ofNullable(attendee);
    }

    public boolean existsById(EventAttendeeId id) {
        return findById(id).isPresent();
    }

    public List<EventAttendee> findAll() {
        return dynamoDbTemplate.scanAll(EventAttendee.class).items().stream().toList();
    }

    public Iterable<EventAttendee> findAllById(Iterable<EventAttendeeId> ids) {
        java.util.List<EventAttendee> list = new java.util.ArrayList<>();
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

    public void delete(EventAttendee attendee) {
        dynamoDbTemplate.delete(attendee);
    }

    public void deleteAllById(Iterable<? extends EventAttendeeId> ids) {
        for (EventAttendeeId id : ids) {
            deleteById(id);
        }
    }

    public void deleteAll(Iterable<? extends EventAttendee> entities) {
        for (EventAttendee entity : entities) {
            delete(entity);
        }
    }

    public void deleteAll() {
        deleteAll(findAll());
    }

    public List<EventAttendee> findByUid(String uid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder().partitionValue(uid).build()
                        ))
                        .build(),
                EventAttendee.class
        ).items().stream().toList();
    }

    public List<EventAttendee> findByEventUid(String eventUid) {
        return findAll().stream()
                .filter(ea -> Objects.equals(ea.getEventUid(), eventUid))
                .toList();
    }

    public List<EventAttendee> findByPersonUid(String personUid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder().partitionValue(personUid).build()
                        ))
                        .build(),
                EventAttendee.class,
                "event-person-index"
        ).items().stream().toList();
    }

    public List<EventAttendee> findByEventUidAndPersonUid(String eventUid, String personUid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder()
                                        .partitionValue(personUid)
                                        .sortValue(eventUid)
                                        .build()
                        ))
                        .build(),
                EventAttendee.class,
                "event-person-index"
        ).items().stream().toList();
    }
}
