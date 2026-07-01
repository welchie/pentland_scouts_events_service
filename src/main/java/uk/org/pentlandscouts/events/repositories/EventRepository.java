package uk.org.pentlandscouts.events.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventId;

import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {

    @Autowired
    private DynamoDbTemplate dynamoDbTemplate;

    public Event save(Event event) {
        dynamoDbTemplate.save(event);
        return event;
    }

    public <S extends Event> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    public Optional<Event> findById(EventId eventId) {
        if (eventId == null || eventId.getUid() == null || eventId.getSortKey() == null) {
            return Optional.empty();
        }
        Event event = dynamoDbTemplate.load(
                Key.builder()
                        .partitionValue(eventId.getUid())
                        .sortValue(eventId.getSortKey())
                        .build(),
                Event.class
        );
        return Optional.ofNullable(event);
    }

    public boolean existsById(EventId eventId) {
        return findById(eventId).isPresent();
    }

    public List<Event> findAll() {
        return dynamoDbTemplate.scanAll(Event.class).items().stream().toList();
    }

    public Iterable<Event> findAllById(Iterable<EventId> ids) {
        java.util.List<Event> list = new java.util.ArrayList<>();
        for (EventId id : ids) {
            findById(id).ifPresent(list::add);
        }
        return list;
    }

    public long count() {
        return findAll().size();
    }

    public void deleteById(EventId eventId) {
        findById(eventId).ifPresent(this::delete);
    }

    public void delete(Event event) {
        dynamoDbTemplate.delete(event);
    }

    public void deleteAllById(Iterable<? extends EventId> ids) {
        for (EventId id : ids) {
            deleteById(id);
        }
    }

    public void deleteAll(Iterable<? extends Event> entities) {
        for (Event entity : entities) {
            delete(entity);
        }
    }

    public void deleteAll() {
        deleteAll(findAll());
    }

    public List<Event> findByNameAndVenue(String name, String venue) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder()
                                        .partitionValue(venue)
                                        .sortValue(name)
                                        .build()
                        ))
                        .build(),
                Event.class,
                "name-venue-index"
        ).items().stream().toList();
    }

    public List<Event> findByUid(String uid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder().partitionValue(uid).build()
                        ))
                        .build(),
                Event.class
        ).items().stream().toList();
    }
}
