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
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.Event;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventRepositoryTest {

    @Mock
    private DynamoDbTemplate dynamoDbTemplate;

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private AwsProperties awsProperties;

    private EventRepository repository;

    @BeforeEach
    public void setUp() {
        when(awsProperties.getTablePrefix()).thenReturn("prefix_");
        repository = new EventRepository(dynamoDbTemplate, enhancedClient, awsProperties);
    }

    @Test
    public void testConstructor() {
        assertNotNull(repository);
    }

    @Test
    public void testFindByNameAndVenue() {
        Event event = new Event("Camp", "Venue", "start", "end");
        PageIterable<Event> pageIterable = mock(PageIterable.class);
        SdkIterable<Event> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(Event.class), eq("name-venue-index")))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(event));

        List<Event> result = repository.findByNameAndVenue("Camp", "Venue");
        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
    }

    @Test
    public void testFindByUid() {
        Event event = new Event("Camp", "Venue", "start", "end");
        PageIterable<Event> pageIterable = mock(PageIterable.class);
        SdkIterable<Event> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(Event.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(event));

        List<Event> result = repository.findByUid("uid-1");
        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
    }
}
