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
import uk.org.pentlandscouts.events.model.Person;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonRepositoryTest {

    @Mock
    private DynamoDbTemplate dynamoDbTemplate;

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private AwsProperties awsProperties;

    private PersonRepository repository;

    @BeforeEach
    public void setUp() {
        when(awsProperties.getTablePrefix()).thenReturn("prefix_");
        repository = new PersonRepository(dynamoDbTemplate, enhancedClient, awsProperties);
    }

    @Test
    public void testConstructor() {
        assertNotNull(repository);
    }

    @Test
    public void testFindByOrderBySortKeyAsc() {
        Person person = new Person("John", "Doe", "dob", "key");
        PageIterable<Person> pageIterable = mock(PageIterable.class);
        SdkIterable<Person> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.scanAll(Person.class)).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(person));

        List<Person> result = repository.findByOrderBySortKeyAsc();
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByFirstNameAndLastName() {
        Person person = new Person("John", "Doe", "dob", "key");
        PageIterable<Person> pageIterable = mock(PageIterable.class);
        SdkIterable<Person> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(Person.class), eq("firstname-lastname-index")))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(person));

        List<Person> result = repository.findByFirstNameAndLastName("John", "Doe");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByFirstNameAndLastNameAndDob() {
        Person person = new Person("John", "Doe", "01/01/2010", "key");
        PageIterable<Person> pageIterable = mock(PageIterable.class);
        SdkIterable<Person> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(Person.class), eq("firstname-lastname-index")))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(person));

        List<Person> result = repository.findByFirstNameAndLastNameAndDob("John", "Doe", "01/01/2010");
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByUid() {
        Person person = new Person("John", "Doe", "dob", "key");
        PageIterable<Person> pageIterable = mock(PageIterable.class);
        SdkIterable<Person> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.query(any(QueryEnhancedRequest.class), eq(Person.class)))
                .thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(person));

        List<Person> result = repository.findByUid("uid-1");
        assertEquals(1, result.size());
    }
}
