package uk.org.pentlandscouts.events.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextRefreshedEvent;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DynamoDBTableInitializerTest {

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private AwsProperties awsProperties;

    @InjectMocks
    private DynamoDBTableInitializer tableInitializer;

    @Mock
    private ContextRefreshedEvent contextRefreshedEvent;

    @Mock
    private DynamoDbTable<Person> personTable;

    @Mock
    private DynamoDbTable<Event> eventTable;

    @Mock
    private DynamoDbTable<EventAttendee> attendeeTable;

    @Mock
    private DynamoDbTable<EventAttendeeHist> histTable;

    @BeforeEach
    public void setUp() {
        lenient().when(enhancedClient.table(eq("test_Person"), any(TableSchema.class))).thenReturn(personTable);
        lenient().when(enhancedClient.table(eq("test_Event"), any(TableSchema.class))).thenReturn(eventTable);
        lenient().when(enhancedClient.table(eq("test_EventAttendee"), any(TableSchema.class))).thenReturn(attendeeTable);
        lenient().when(enhancedClient.table(eq("test_EventAttendeeHist"), any(TableSchema.class))).thenReturn(histTable);
    }

    @Test
    public void testOnApplicationEventNonLocal() {
        when(awsProperties.getEndPointURL()).thenReturn("https://dynamodb.us-east-1.amazonaws.com");

        tableInitializer.onApplicationEvent(contextRefreshedEvent);

        verifyNoInteractions(enhancedClient);
    }

    @Test
    public void testOnApplicationEventLocalSuccess() {
        when(awsProperties.getEndPointURL()).thenReturn("http://localhost:8000");
        when(awsProperties.getTablePrefix()).thenReturn("test_");

        tableInitializer.onApplicationEvent(contextRefreshedEvent);

        verify(personTable).deleteTable();
        verify(eventTable).deleteTable();
        verify(attendeeTable).deleteTable();
        verify(histTable).deleteTable();

        verify(personTable).createTable(any(software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest.class));
        verify(eventTable).createTable(any(software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest.class));
        verify(attendeeTable).createTable(any(software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest.class));
        verify(histTable).createTable(any(software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest.class));
    }

    @Test
    public void testOnApplicationEventDeleteTableNotFound() {
        when(awsProperties.getEndPointURL()).thenReturn("http://localhost:8000");
        when(awsProperties.getTablePrefix()).thenReturn("test_");

        doThrow(ResourceNotFoundException.builder().message("not found").build())
                .when(personTable).deleteTable();

        tableInitializer.onApplicationEvent(contextRefreshedEvent);

        // Even if Person table is not found, other tables should be attempted
        verify(eventTable).deleteTable();
        verify(personTable).createTable(any(software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest.class));
    }
}
