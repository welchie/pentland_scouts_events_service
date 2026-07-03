package uk.org.pentlandscouts.events.repositories;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.EventAttendee;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventAttendeeRepositoryTest {

    @Mock
    private DynamoDbTemplate dynamoDbTemplate;

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private AwsProperties awsProperties;

    private EventAttendeeRepository repository;

    @BeforeEach
    public void setUp() {
        when(awsProperties.getTablePrefix()).thenReturn("prefix_");
        repository = new EventAttendeeRepository(dynamoDbTemplate, enhancedClient, awsProperties);
    }

    @Test
    public void testConstructor() {
        assertNotNull(repository);
    }

    @Test
    public void testFindByUid() {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        PageIterable<EventAttendee> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendee> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(EventAttendee.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(attendee));

        List<EventAttendee> result = repository.findByUid("uid-1");
        assertEquals(1, result.size());
        assertEquals(attendee, result.get(0));
    }

    @Test
    public void testFindByEventUid() {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        PageIterable<EventAttendee> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendee> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.scan(any(ScanEnhancedRequest.class), eq(EventAttendee.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(attendee));

        List<EventAttendee> result = repository.findByEventUid("event-1");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByPersonUid() {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        PageIterable<EventAttendee> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendee> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(EventAttendee.class), eq("event-person-index")))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(attendee));

        List<EventAttendee> result = repository.findByPersonUid("person-1");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByEventUidAndPersonUid() {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        PageIterable<EventAttendee> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendee> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(EventAttendee.class), eq("event-person-index")))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(attendee));

        List<EventAttendee> result = repository.findByEventUidAndPersonUid("event-1", "person-1");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByEventUidAndCheckedIn() {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        PageIterable<EventAttendee> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendee> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.scan(any(ScanEnhancedRequest.class), eq(EventAttendee.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(attendee));

        List<EventAttendee> result = repository.findByEventUidAndCheckedIn("event-1", "true");
        assertEquals(1, result.size());
    }
}
