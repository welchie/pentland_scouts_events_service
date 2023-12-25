package uk.org.pentlandscouts.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.exception.PersonException;
import uk.org.pentlandscouts.events.exception.PersonNotFoundException;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.domain.PersonalDetails;
import uk.org.pentlandscouts.events.service.PersonService;

import java.util.*;

@RestController
@RequestMapping("/person")

public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    PersonService personService;

    private static final String TABLE_NAME = "Person";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";

    private static final String PERSONAL_DETAILS = "PersonalDetails";

    @GetMapping("/personaldetails/{uid}")
    public ResponseEntity<Object> getPerson(@PathVariable("uid") String uid) throws PersonNotFoundException {

        Map<String, List<PersonalDetails>> response = new HashMap<>(1);

        try {
            if (!uid.isEmpty()) {
                List<Person> personList = personService.findByUid(uid);
                if (personList.size() >0 && personList.get(0) == null) {
                    throw new PersonNotFoundException(uid);
                }

                //Convert Person to JSON
                Person p = personList.get(0);
                PersonalDetails personalDetails = new PersonalDetails();
                personalDetails.setUid(p.getUid());
                personalDetails.setFirstName(p.getFirstName());
                personalDetails.setLastName(p.getLastName());
                personalDetails.setDob(p.getDob());
                personalDetails.setPosition(p.getPosition());
                personalDetails.setScoutGroup(p.getScoutGroup());
                personalDetails.setScoutSection(p.getScoutSection());
                personalDetails.setSectionName(p.getSectionName());

                List<PersonalDetails> details = new ArrayList<>();
                details.add(personalDetails);

                response.put(TABLE_NAME, details);

                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (PersonNotFoundException pe) {
            return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Find all People
     *
     * @return
     */
    @GetMapping("/all")
    public List<Person> findAll() {
        return personService.findAll();
    }

    @GetMapping("/find")
    ResponseEntity<Object> findByFirstNameLastNameDob(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "dob") String dob) {
        Map<String, List<Person>> response = new HashMap<>(1);
        try {
            if (!firstName.isEmpty() && !lastName.isEmpty() && !dob.isEmpty()) {

                List<Person> personList = personService.findByFirstNameAndLastNameAndDob(firstName, lastName, dob);
                if (personList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, personList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/create/personaldetails")
    public ResponseEntity<Object> create(@RequestParam(value = "firstName") String firstName,
                                         @RequestParam(value = "lastName") String lastName,
                                         @RequestParam(value = "dob") String dob,
                                         @RequestParam(value = "scoutSection") String scoutSection,
                                         @RequestParam(value = "sectionName") String sectionName,
                                         @RequestParam(value = "scoutGroup") String scoutGroup,
                                         @RequestParam(value= "position") String position) {
        try {

            Person result = null;
            //Lookup the DB for an existing record
            List<Person> lookUpPerson = personService.findByFirstNameAndLastNameAndDob(firstName, lastName, dob);
            if (lookUpPerson.size() == 0) {
                //Person not found create new record

                Person person = new Person(firstName, lastName, dob,PERSONAL_DETAILS);
                person.setSortKey(firstName + lastName + dob);
                person.setScoutGroup(scoutGroup);
                person.setScoutSection(scoutSection);
                person.setSectionName(sectionName);
                person.setPosition(position);
                logger.info("Creating new record: {}", person);


                result = personService.createRecord(person);
            } else {
                result = lookUpPerson.get(0);
            }

            Map<String, Person> response = new HashMap<>(1);
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public Person update(@RequestBody Person updPerson) {
        try {

            if (updPerson != null && !updPerson.getUid().isEmpty()) {
                //Find if the record exists
                List<Person> currentPerson = personService.findByUid(updPerson.getUid());
                if (!currentPerson.isEmpty()) {
                    return personService.update(updPerson);
                } else {

                    logger.info("Person not found in the db: {}. No updates", updPerson);
                    throw new PersonException("Person not found in the db: " + updPerson + ". No updates");
                }
            }
        } catch (PersonException ex) {
            logger.error(ex.getMessage());
        }
        finally {
            return updPerson;
        }
    }
}
