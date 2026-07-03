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
import uk.org.pentlandscouts.events.model.EventAttendeeHist;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventAttendeeHistRepositoryTest {

    @Mock
    private DynamoDbTemplate dynamoDbTemplate;

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private AwsProperties awsProperties;

    private EventAttendeeHistRepository repository;

    @BeforeEach
    public void setUp() {
        when(awsProperties.getTablePrefix()).thenReturn("prefix_");
        repository = new EventAttendeeHistRepository(dynamoDbTemplate, enhancedClient, awsProperties);
    }

    @Test
    public void testConstructor() {
        assertNotNull(repository);
    }

    @Test
    public void testFindByUid() {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        PageIterable<EventAttendeeHist> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendeeHist> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(EventAttendeeHist.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(hist));

        List<EventAttendeeHist> result = repository.findByUid("uid-1");
        assertEquals(1, result.size());
        assertEquals(hist, result.get(0));
    }

    @Test
    public void testFindByEventUid() {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        PageIterable<EventAttendeeHist> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendeeHist> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.scan(any(ScanEnhancedRequest.class), eq(EventAttendeeHist.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(hist));

        List<EventAttendeeHist> result = repository.findByEventUid("event-1");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByPersonUid() {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        PageIterable<EventAttendeeHist> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendeeHist> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(EventAttendeeHist.class), eq("event-person-index")))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(hist));

        List<EventAttendeeHist> result = repository.findByPersonUid("person-1");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByEventUidAndPersonUid() {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        PageIterable<EventAttendeeHist> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendeeHist> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(EventAttendeeHist.class), eq("event-person-index")))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(hist));

        List<EventAttendeeHist> result = repository.findByEventUidAndPersonUid("event-1", "person-1");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByEventUidAndCheckedIn() {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        PageIterable<EventAttendeeHist> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendeeHist> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.scan(any(ScanEnhancedRequest.class), eq(EventAttendeeHist.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(hist));

        List<EventAttendeeHist> result = repository.findByEventUidAndCheckedIn("event-1", "true");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByHistDateBetween() {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        hist.setHistDate("15/05/2026");

        PageIterable<EventAttendeeHist> pageIterable = mock(PageIterable.class);
        SdkIterable<EventAttendeeHist> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.scanAll(EventAttendeeHist.class)).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(hist));

        List<EventAttendeeHist> result = repository.findByHistDateBetween("10/05/2026", "20/05/2026");
        assertEquals(1, result.size());
    }
}
