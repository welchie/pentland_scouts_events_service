package uk.org.pentlandscouts.events.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import uk.org.pentlandscouts.events.model.Person;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DynamoDBConfigTest {

    @Mock
    private AwsProperties awsProperties;

    @InjectMocks
    private DynamoDBConfig config;

    @Test
    public void testResolveTableName() {
        when(awsProperties.getTablePrefix()).thenReturn("test_");
        DynamoDbTableNameResolver resolver = config.dynamoDbTableNameResolver(awsProperties);
        assertNotNull(resolver);
        assertEquals("test_Person", resolver.resolve(Person.class));
    }

    @Test
    public void testResolveTableNameNullPrefix() {
        when(awsProperties.getTablePrefix()).thenReturn(null);
        DynamoDbTableNameResolver resolver = config.dynamoDbTableNameResolver(awsProperties);
        assertNotNull(resolver);
        assertEquals("Person", resolver.resolve(Person.class));
    }

    @Test
    public void testDynamoDbEnhancedClient() {
        DynamoDbClient client = mock(DynamoDbClient.class);
        DynamoDbEnhancedClient enhanced = config.dynamoDbEnhancedClient(client);
        assertNotNull(enhanced);
    }

    @Test
    public void testDynamoDbTemplate() {
        DynamoDbEnhancedClient enhanced = mock(DynamoDbEnhancedClient.class);
        DynamoDbTableNameResolver resolver = mock(DynamoDbTableNameResolver.class);
        DynamoDbTemplate template = config.dynamoDbTemplate(enhanced, resolver);
        assertNotNull(template);
    }

    @Test
    public void testDynamoDbClient() {
        when(awsProperties.getAccessKey()).thenReturn("access");
        when(awsProperties.getSecretKey()).thenReturn("secret");
        when(awsProperties.getRegion()).thenReturn("us-east-1");
        when(awsProperties.getEndPointURL()).thenReturn("http://localhost:8000");

        DynamoDbClient client = config.dynamoDbClient();
        assertNotNull(client);
        client.close();
    }
}
