package uk.org.pentlandscouts.events.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.EventAttendeeId;

import java.util.List;

@Repository
public class EventAttendeeRepository extends AbstractDynamoDbRepository<EventAttendee, EventAttendeeId> {

    @Autowired
    public EventAttendeeRepository(DynamoDbTemplate dynamoDbTemplate, DynamoDbEnhancedClient enhancedClient, AwsProperties awsProperties) {
        super(dynamoDbTemplate, enhancedClient, EventAttendee.class,
              id -> Key.builder().partitionValue(id.getUid()).sortValue(id.getSortKey()).build(),
              (awsProperties.getTablePrefix() != null ? awsProperties.getTablePrefix() : "") + "EventAttendee");
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
        return dynamoDbTemplate.scan(
                ScanEnhancedRequest.builder()
                        .filterExpression(Expression.builder()
                                .expression("eventUid = :val")
                                .putExpressionValue(":val", AttributeValue.builder().s(eventUid).build())
                                .build())
                        .build(),
                EventAttendee.class
        ).items().stream().toList();
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

    public List<EventAttendee> findByEventUidAndCheckedIn(String eventUid, String checkedIn) {
        return dynamoDbTemplate.scan(
                ScanEnhancedRequest.builder()
                        .filterExpression(Expression.builder()
                                .expression("eventUid = :eventVal AND checkedIn = :checkedVal")
                                .putExpressionValue(":eventVal", AttributeValue.builder().s(eventUid).build())
                                .putExpressionValue(":checkedVal", AttributeValue.builder().s(checkedIn).build())
                                .build())
                        .build(),
                EventAttendee.class
        ).items().stream().toList();
    }
}
