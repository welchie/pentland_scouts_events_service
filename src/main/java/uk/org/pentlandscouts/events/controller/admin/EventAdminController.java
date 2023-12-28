package uk.org.pentlandscouts.events.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.exception.AwsPropertiesException;
import uk.org.pentlandscouts.events.exception.EventException;
import uk.org.pentlandscouts.events.exception.PersonException;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/")
public class EventAdminController {

    private  static final Logger logger = LoggerFactory.getLogger(EventAdminController.class);

    @Autowired
    AwsProperties awsProperties;

    private static final String PERSON_TABLE_NAME = "Person";

    private static final String EVENT_TABLE_NAME = "Event";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";
    @GetMapping(value="/create/persontable")
    public ResponseEntity<Object> createSensorDataTable() throws URISyntaxException, AwsPropertiesException {
        if (EventUtils.isEmpty(awsProperties.getRegion()) ||
                EventUtils.isEmpty(awsProperties.getAccessKey()) ||
                EventUtils.isEmpty(awsProperties.getSecretKey()))
        {
            throw new AwsPropertiesException("Unable to get AWS Properties");
        }
        logger.info("Creating Person table in DynamoDB...");
        AwsCredentialsProvider creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey()));

        DynamoDbClient dbClient;
        if (!awsProperties.getEndPointURL().isEmpty())
        {
            dbClient = DynamoDbClient.builder()
                    .region(Region.of(awsProperties.getRegion()))
                    .endpointOverride(new URI(awsProperties.getEndPointURL()) )
                    .credentialsProvider(creds)
                    .build();
        }
        else
        {
            dbClient = DynamoDbClient.builder()
                    .region(Region.of(awsProperties.getRegion()))
                    .credentialsProvider(creds)
                    .build();
        }

        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(dbClient)
                        .build();

        DynamoDbTable<Person> personTable =
                enhancedClient.table(PERSON_TABLE_NAME, TableSchema.fromBean(Person.class));


        try
        {
            String responseText =createPersonTable(personTable,dbClient);
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> result = new ArrayList<>();
            result.add(responseText);
            response.put(RESULT_TITLE,result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (PersonException e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String createPersonTable(DynamoDbTable<Person> sensorDataDynamoDbTable, DynamoDbClient dynamoDbClient) throws PersonException{
        // Create the DynamoDB table by using the 'customerDynamoDbTable' DynamoDbTable instance.
        try{
            sensorDataDynamoDbTable.createTable(builder -> builder
                    .globalSecondaryIndices(gsi -> gsi.indexName("firstname-lastname-index")
                            // 3. Populate the GSI with all attributes.
                            .projection(p -> p
                                    .projectionType(ProjectionType.ALL))
                            .provisionedThroughput(b -> b
                                    .readCapacityUnits(10L)
                                    .writeCapacityUnits(10L)
                                    .build())
                    )
                    .provisionedThroughput(b -> b
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
            );

            // The 'dynamoDbClient' instance that's passed to the builder for the DynamoDbWaiter is the same instance
            // that was passed to the builder of the DynamoDbEnhancedClient instance used to create the 'customerDynamoDbTable'.
            // This means that the same Region that was configured on the standard 'dynamoDbClient' instance is used for all service clients.
            try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build()) { // DynamoDbWaiter is Autocloseable
                ResponseOrException<DescribeTableResponse> response = waiter
                        .waitUntilTableExists(builder -> builder.tableName(PERSON_TABLE_NAME).build())
                        .matched();
                DescribeTableResponse tableDescription = response.response().orElseThrow(
                        () -> new RuntimeException("Person table was not created."));
                // The actual error can be inspected in response.exception()
                logger.info("Table {} created. {}", PERSON_TABLE_NAME, tableDescription);
            }


        } catch (ResourceInUseException e) {
            throw new PersonException(e.getMessage());

        }

        logger.info("Person table was created.");
        return "Person table was created";
    }


    @GetMapping(value="/create/eventtable")
    public ResponseEntity<Object> createEventDataTable() throws URISyntaxException, AwsPropertiesException {
        if (EventUtils.isEmpty(awsProperties.getRegion()) ||
                EventUtils.isEmpty(awsProperties.getAccessKey()) ||
                EventUtils.isEmpty(awsProperties.getSecretKey()))
        {
            throw new AwsPropertiesException("Unable to get AWS Properties");
        }
        logger.info("Creating Event table in DynamoDB...");
        AwsCredentialsProvider creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey()));

        DynamoDbClient dbClient;
        if (!awsProperties.getEndPointURL().isEmpty())
        {
            dbClient = DynamoDbClient.builder()
                    .region(Region.of(awsProperties.getRegion()))
                    .endpointOverride(new URI(awsProperties.getEndPointURL()) )
                    .credentialsProvider(creds)
                    .build();
        }
        else
        {
            dbClient = DynamoDbClient.builder()
                    .region(Region.of(awsProperties.getRegion()))
                    .credentialsProvider(creds)
                    .build();
        }

        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(dbClient)
                        .build();

        DynamoDbTable<Event> eventTable =
                enhancedClient.table(EVENT_TABLE_NAME, TableSchema.fromBean(Event.class));


        try
        {
            String responseText =createEventTable(eventTable,dbClient);
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> result = new ArrayList<>();
            result.add(responseText);
            response.put(RESULT_TITLE,result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (EventException e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String createEventTable(DynamoDbTable<Event> eventDynamoDbTable, DynamoDbClient dynamoDbClient) throws EventException {
        // Create the DynamoDB table by using the 'customerDynamoDbTable' DynamoDbTable instance.
        try{
            eventDynamoDbTable.createTable(builder -> builder
                    .globalSecondaryIndices(gsi -> gsi.indexName("name-venue-index")
                            // 3. Populate the GSI with all attributes.
                            .projection(p -> p
                                    .projectionType(ProjectionType.ALL))
                            .provisionedThroughput(b -> b
                                    .readCapacityUnits(10L)
                                    .writeCapacityUnits(10L)
                                    .build())
                    )
                    .provisionedThroughput(b -> b
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
            );

            // The 'dynamoDbClient' instance that's passed to the builder for the DynamoDbWaiter is the same instance
            // that was passed to the builder of the DynamoDbEnhancedClient instance used to create the 'customerDynamoDbTable'.
            // This means that the same Region that was configured on the standard 'dynamoDbClient' instance is used for all service clients.
            try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build()) { // DynamoDbWaiter is Autocloseable
                ResponseOrException<DescribeTableResponse> response = waiter
                        .waitUntilTableExists(builder -> builder.tableName(EVENT_TABLE_NAME).build())
                        .matched();
                DescribeTableResponse tableDescription = response.response().orElseThrow(
                        () -> new RuntimeException("Event table was not created."));
                // The actual error can be inspected in response.exception()
                logger.info("Table {} created. {}", EVENT_TABLE_NAME, tableDescription);
            }


        } catch (ResourceInUseException e) {
            throw new EventException(e.getMessage());

        }

        logger.info("Event table was created.");
        return "Event table was created";
    }

}
