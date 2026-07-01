package uk.org.pentlandscouts.events.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventId;

import java.util.List;

@Repository
public class EventRepository extends AbstractDynamoDbRepository<Event, EventId> {

    @Autowired
    public EventRepository(DynamoDbTemplate dynamoDbTemplate, DynamoDbEnhancedClient enhancedClient, AwsProperties awsProperties) {
        super(dynamoDbTemplate, enhancedClient, Event.class,
              id -> Key.builder().partitionValue(id.getUid()).sortValue(id.getSortKey()).build(),
              (awsProperties.getTablePrefix() != null ? awsProperties.getTablePrefix() : "") + "Event");
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
