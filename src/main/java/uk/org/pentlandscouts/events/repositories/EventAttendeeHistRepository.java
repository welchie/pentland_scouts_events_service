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
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.model.EventAttendeeId;

import java.util.List;

@Repository
public class EventAttendeeHistRepository extends AbstractDynamoDbRepository<EventAttendeeHist, EventAttendeeId> {

    @Autowired
    public EventAttendeeHistRepository(DynamoDbTemplate dynamoDbTemplate, DynamoDbEnhancedClient enhancedClient, AwsProperties awsProperties) {
        super(dynamoDbTemplate, enhancedClient, EventAttendeeHist.class,
              id -> Key.builder().partitionValue(id.getUid()).sortValue(id.getSortKey()).build(),
              (awsProperties.getTablePrefix() != null ? awsProperties.getTablePrefix() : "") + "EventAttendeeHist");
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
        return dynamoDbTemplate.scan(
                ScanEnhancedRequest.builder()
                        .filterExpression(Expression.builder()
                                .expression("eventUid = :val")
                                .putExpressionValue(":val", AttributeValue.builder().s(eventUid).build())
                                .build())
                        .build(),
                EventAttendeeHist.class
        ).items().stream().toList();
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

    public List<EventAttendeeHist> findByEventUidAndCheckedIn(String eventUid, String checkedIn) {
        return dynamoDbTemplate.scan(
                ScanEnhancedRequest.builder()
                        .filterExpression(Expression.builder()
                                .expression("eventUid = :eventVal AND checkedIn = :checkedVal")
                                .putExpressionValue(":eventVal", AttributeValue.builder().s(eventUid).build())
                                .putExpressionValue(":checkedVal", AttributeValue.builder().s(checkedIn).build())
                                .build())
                        .build(),
                EventAttendeeHist.class
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
