package uk.org.pentlandscouts.events.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;

@Component
public class DynamoDBTableInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBTableInitializer.class);

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    @Autowired
    private AwsProperties awsProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String endpoint = awsProperties.getEndPointURL();
        if (endpoint != null && (endpoint.contains("localhost") || endpoint.contains("127.0.0.1"))) {
            logger.info("Initializing local DynamoDB tables on localhost endpoint: {}...", endpoint);
            String prefix = awsProperties.getTablePrefix() != null ? awsProperties.getTablePrefix() : "";
            
            // Delete tables first to ensure clean state with latest GSI schema
            deleteTableSafe(prefix + "Person", Person.class);
            deleteTableSafe(prefix + "Event", Event.class);
            deleteTableSafe(prefix + "EventAttendee", EventAttendee.class);
            deleteTableSafe(prefix + "EventAttendeeHist", EventAttendeeHist.class);

            // Create tables fresh
            createTable(Person.class, prefix + "Person");
            createTable(Event.class, prefix + "Event");
            createTable(EventAttendee.class, prefix + "EventAttendee");
            createTable(EventAttendeeHist.class, prefix + "EventAttendeeHist");
        } else {
            logger.info("Skipping DynamoDB auto-initialization for non-local endpoint: {}", endpoint);
        }
    }

    private <T> void deleteTableSafe(String tableName, Class<T> entityClass) {
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(entityClass));
            table.deleteTable();
            logger.info("Successfully deleted table: {}", tableName);
        } catch (ResourceNotFoundException e) {
            // Table did not exist
        } catch (Exception e) {
            logger.error("Failed to delete table {}", tableName, e);
        }
    }

    private <T> void createTable(Class<T> entityClass, String tableName) {
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(entityClass));
            
            ProvisionedThroughput throughput = ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L)
                    .build();
            
            CreateTableEnhancedRequest.Builder requestBuilder = CreateTableEnhancedRequest.builder()
                    .provisionedThroughput(throughput);
            
            if (entityClass == Person.class) {
                requestBuilder.globalSecondaryIndices(
                        EnhancedGlobalSecondaryIndex.builder()
                                .indexName("firstname-lastname-index")
                                .provisionedThroughput(throughput)
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .build()
                );
            } else if (entityClass == Event.class) {
                requestBuilder.globalSecondaryIndices(
                        EnhancedGlobalSecondaryIndex.builder()
                                .indexName("name-venue-index")
                                .provisionedThroughput(throughput)
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .build()
                );
            } else if (entityClass == EventAttendee.class || entityClass == EventAttendeeHist.class) {
                requestBuilder.globalSecondaryIndices(
                        EnhancedGlobalSecondaryIndex.builder()
                                .indexName("event-person-index")
                                .provisionedThroughput(throughput)
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .build()
                );
            }
            
            table.createTable(requestBuilder.build());
            logger.info("Successfully created table: {}", tableName);
        } catch (Exception e) {
            logger.error("Failed to create table {}", tableName, e);
        }
    }
}
