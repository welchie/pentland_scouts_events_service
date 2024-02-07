package uk.org.pentlandscouts.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.exception.PersonException;
import uk.org.pentlandscouts.events.exception.PersonNotFoundException;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.domain.PersonDomain;
import uk.org.pentlandscouts.events.model.domain.PersonalDetails;
import uk.org.pentlandscouts.events.service.EventAttendeeService;
import uk.org.pentlandscouts.events.service.PersonService;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/person")

public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    PersonService personService;

    @Autowired
    EventAttendeeService eventAttendeeService;

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
    public ResponseEntity<Object> findAll() {

        Map<String, List<Person>> response = new HashMap<>(1);
        try {
                List<Person> personList = personService.findAll();
                if (personList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, personList);
                return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/{subcamp}")
    public ResponseEntity<Object> findAllBySubCamp(@PathVariable("subcamp") String subcamp) {

        Map<String, List<PersonDomain>> response = new HashMap<>(1);
        try {
            List<Person> personList = personService.findAllBySubCamp(subcamp);
            if (personList.isEmpty()) {
                return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
            }

            List<PersonDomain> pdList = new ArrayList<PersonDomain>();
            //Convert to PersonDomain and look up CheckedIN status from Event Attendee
            for(Person p: personList)
            {
                //Look up EventAttendee
                List<EventAttendee> results = eventAttendeeService.findByPersonUid(p.getUid());
                if (results.size() > 0) {
                    PersonDomain pd = new PersonDomain(p.getFirstName(), p.getLastName(), p.getDob(), p.getSortKey(), p.getUid());
                    pd.setAllergies(p.getAllergies());
                    pd.setContactEmail(p.getContactEmail());
                    pd.setDietary(p.getDietary());
                    pd.setPhotoPermission(p.getPhotoPermission());
                    pd.setCheckedIn(results.get(0).getCheckedIn());
                    pd.setContactPhoneNo(p.getContactPhoneNo());
                    pd.setMedicine(p.getMedicine());
                    pd.setSubCamp(p.getSubCamp());
                    pd.setEmergencyRelationship(p.getEmergencyRelationship());
                    pd.setEmergencyContactNo(p.getEmergencyContactNo());
                    pd.setEmergencyContactName(p.getEmergencyContactName());
                    pd.setPosition(p.getPosition());
                    pd.setScoutGroup(p.getScoutGroup());
                    pd.setScoutSection(p.getScoutSection());
                    pd.setSectionName(p.getSectionName());


                    pdList.add(pd);
                }
            }

            response.put(TABLE_NAME, pdList);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @GetMapping("/find")
//    ResponseEntity<Object> findByFirstNameLastNameDob(
//            @RequestParam(value = "firstName") String firstName,
//            @RequestParam(value = "lastName") String lastName,
//            @RequestParam(value = "dob") String dob) {
//        Map<String, List<Person>> response = new HashMap<>(1);
//        try {
//            if (!firstName.isEmpty() && !lastName.isEmpty() && !dob.isEmpty()) {
//
//                List<Person> personList = personService.findByFirstNameAndLastNameAndDob(firstName, lastName, dob);
//                if (personList.isEmpty()) {
//                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
//                }
//                response.put(TABLE_NAME, personList);
//                return new ResponseEntity<>(response, HttpStatus.OK);
//
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
//            List<String> errors = new ArrayList<>();
//            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
//            exceptionResponse.put(ERROR_TITLE, errors);
//            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @GetMapping("/find")
    ResponseEntity<Object> findByFirstNameLastName(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName) {
        Map<String, List<Person>> response = new HashMap<>(1);
        try {
            if (!firstName.isEmpty() && !lastName.isEmpty() ) {

                List<Person> personList = personService.findByFirstNameAndLastName(firstName, lastName);
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

    @GetMapping("/find/{uid}")
    ResponseEntity<Object> findByUid(
            @PathVariable("uid") String uid) {
        Map<String, List<Person>> response = new HashMap<>(1);
        try {
            if (!uid.isEmpty()) {

                List<Person> personList = personService.findByUid(uid);
                if (personList.size() >0 && personList.get(0) == null) {
                    throw new PersonNotFoundException(uid);
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

    @PostMapping(value = "/create")
    public ResponseEntity<Object> createPerson(@RequestBody Person person) {
        try {

            Person result = null;
            //Lookup the DB for an existing record
            List<Person> lookUpPerson = personService.findByFirstNameAndLastNameAndDob(person.getFirstName(),
                                                                                person.getLastName(),
                                                                                person.getDob());
            if (lookUpPerson.size() == 0) {
                //Person not found create new record
                person.setUid(EventUtils.generateType1UUID().toString());
                person.setSortKey(person.getFirstName() + person.getLastName() + person.getDob());
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

    @DeleteMapping("/{uid}")
    public ResponseEntity<Object> removePerson(@PathVariable("uid") String uid) throws PersonNotFoundException {

        Map<String, List<String>> response = new HashMap<>(1);

        try {
            if (!uid.isEmpty()) {
                List<Person> personList = personService.findByUid(uid);
                if (personList.size() >0 && personList.get(0) == null) {
                    throw new PersonNotFoundException(uid);
                }

                personService.delete(personList.get(0));


                List<String> details = new ArrayList<>();
                details.add("Person with UID: " + uid + " removed");

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
}
