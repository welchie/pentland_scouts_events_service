package uk.org.pentlandscouts.events.importpeople;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.org.pentlandscouts.events.Application;
import uk.org.pentlandscouts.events.controller.VersionController;
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.EventException;
import uk.org.pentlandscouts.events.exception.PersonException;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.service.EventAttendeeService;
import uk.org.pentlandscouts.events.service.EventService;
import uk.org.pentlandscouts.events.service.PersonService;
import uk.org.pentlandscouts.events.utils.ExcelUtils;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-dev.properties")
public class ImportPeopleTest {

    static final String XLS_FILE = "people_import.xlsx";

    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventAttendeeService eventAttendeeService;

    private final ExcelUtils excelUtils = new ExcelUtils();
    @Test
    public void testGetExcelData() throws FileNotFoundException {
        Map<Integer, List<String>> data = excelUtils.importFromExcel(XLS_FILE);
        assertFalse(data.isEmpty());
        for(int i =0 ; i <data.size();i++)
        {
            logger.info(String.valueOf(data.get(i)));
        }
    }

    @Test
    public void testConvertExcelDataToObjects() throws FileNotFoundException, EventException {
        Map<Integer, List<String>> data = excelUtils.importFromExcel(XLS_FILE);
        assertFalse(data.isEmpty());

        String testEventName = "Test Event";
        String testVenue = "Test Venue";
        String startDate = "01/02/2025";
        String endDate = "02/02/2025";
        //Create a Test Event
        Event event = new Event(testEventName, testVenue, startDate, endDate);
        event = eventService.createRecord(event);
        assertTrue(event.getUid() != "");
        logger.info("Event created {}",event);

        List<Person> personList = new ArrayList<Person>();
        for(int i =1 ; i <data.size();i++)
        {
            List<String> row = data.get(i);
            /**
             * Data structure
             *
             * row[0] = firstName
             * row[1] = lastName
             * row[2] = Section
             * row[3] = SubCamp
             * row[4] = Age At Start
             * row[5] = Contact Email
             * row[6] = Photo Permission
             * row[7] = Allergies
             * row[8] = Medical Requirements
             * row[9] = Dietary Requirements
             */

            if (!row.isEmpty()) {
                if (row.get(0) != " ") {
                    String firstName = row.get(0);
                    String lastName = row.get(1);
                    String dob = row.get(4);
                    String sortKey = firstName + lastName + dob;

                    Person p = new Person(firstName,lastName,dob,sortKey);
                    String sectionDetails = row.get(2);
                    String section = "";
                    String group = "";
                    StringTokenizer st = new StringTokenizer(sectionDetails, ":");
                    if (st.countTokens() >= 2) {
                        group =  st.nextToken();
                        section = st.nextToken();
                    }

                    p.setScoutSection(section);
                    p.setScoutGroup(group);
                    p.setSubCamp(row.get(3));
                    p.setContactEmail(row.get(5));
                    String photoPermission = row.get(6);
                    if (photoPermission == " ") photoPermission = "No";
                    p.setPhotoPermission(photoPermission);
                    p.setAllergies(row.get(7));
                    p.setMedicine(row.get(8));
                    p.setDietary(row.get(9));
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
                assertTrue(p.getUid() != "");
                logger.info("Person created {}",p);
                //Create EventAttendee record
                EventAttendee eventAttendee= new EventAttendee(event.getUid(),p.getUid(),p.getPhotoPermission());

                eventAttendee = eventAttendeeService.createRecord(eventAttendee);
                assertTrue(eventAttendee.getUid() != "");
                logger.info("EventAttendee created: {}",eventAttendee);
            }
            catch (PersonException | EventAttendeeException e)
            {
                logger.error(e.getMessage());
            }
        }
    }
}
