package uk.org.pentlandscouts.events.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.PersonException;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.service.EventAttendeeService;
import uk.org.pentlandscouts.events.service.PersonService;
import uk.org.pentlandscouts.events.utils.EventUtils;
import uk.org.pentlandscouts.events.utils.ExcelUtils;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/admin/person")
public class PersonAdminController {

    private  static final Logger logger = LoggerFactory.getLogger(PersonAdminController.class);

    @Autowired
    AwsProperties awsProperties;

    @Autowired
    PersonService personService;

    @Autowired
    EventAttendeeService eventAttendeeService;

    private final ExcelUtils excelUtils = new ExcelUtils();

    private static final String PERSON_TABLE_NAME = "Person";

    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String EVENT_ATTENDEES_TITLE = "EventAttendees";


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

    @GetMapping(value="/import/people")
    public ResponseEntity<Object> importPeople(@RequestParam(value = "fileName") String fileName,
                                               @RequestParam(value = "eventUid") String eventUid) {

        List<Person> personList = new ArrayList<Person>();
        List<String> eventAttendeeRecords = new ArrayList<String>();
        Map<String, List<EventAttendee>> response = new HashMap<>(1);
        List<EventAttendee> results = new ArrayList<>();

        logger.info("Importing people from file: {}...", fileName);
        try {
            Map<Integer, List<String>> data = excelUtils.importFromExcel(fileName);


            for (int i = 1; i < data.size(); i++) {
                List<String> row = data.get(i);
                /**
                 * Data structure
                 *
                 * row[0] = firstName
                 * row[1] = lastName
                 * row[2] = Section
                 * row[3] = Age At Start
                 * row[4] = Contact Email
                 * row[5] = Photo Permission
                 * row[6] = Allergies
                 * row[7] = Medical Requirements
                 * row[8] = Dietary Requirements
                 */

                if (!row.isEmpty()) {
                    if (row.get(0) != " ") {
                        String firstName = row.get(0);
                        String lastName = row.get(1);
                        String dob = row.get(3);
                        String sortKey = firstName + lastName + dob;

                        Person p = new Person(firstName, lastName, dob, sortKey);
                        String sectionDetails = row.get(2);
                        String section = "";
                        String group = "";
                        StringTokenizer st = new StringTokenizer(sectionDetails, ":");
                        if (st.countTokens() >= 2) {
                            group = st.nextToken();
                            section = st.nextToken();
                        }

                        p.setScoutSection(section);
                        p.setScoutGroup(group);
                        p.setContactEmail(row.get(4));
                        String photoPermission = row.get(5).toLowerCase();
                        if (photoPermission.equals("") || photoPermission.equals("no"))
                        {
                            p.setPhotoPermission("false");
                        }
                        else {
                            p.setPhotoPermission("true");
                        }

                        p.setAllergies(row.get(6));
                        p.setMedicine(row.get(7));
                        p.setDietary(row.get(8));
                        p.setSortKey(p.getFirstName() + p.getLastName() + p.getDob());
                        personList.add(p);
                    }
                }
            }



            for(Person p:personList)
            {
                try {
                    //Call person Service to add to the DB
                    p = personService.createRecord(p);
                    if (p.getUid() != "") {
                        logger.info("Person created {}", p);
                        //Create EventAttendee record
                        EventAttendee eventAttendee= new EventAttendee(eventUid,p.getUid(),p.getPhotoPermission());

                        eventAttendee = eventAttendeeService.createRecord(eventAttendee);
                        logger.info("EventAttendee created: {}",eventAttendee);
                        results.add(eventAttendee);
                    }
                    else {
                        logger.error("Error creating Person record {}", p);
                    }
                }
                catch (PersonException e)
                {
                    logger.error(e.getMessage());
                } catch (EventAttendeeException e)
                {
                    logger.error(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("File name found {} : {}", fileName, e.getMessage());
        }
        finally {
            response.put(EVENT_ATTENDEES_TITLE,results);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
