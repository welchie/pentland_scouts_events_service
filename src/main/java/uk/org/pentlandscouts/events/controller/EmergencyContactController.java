package uk.org.pentlandscouts.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.exception.PersonNotFoundException;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.domain.EmergencyContactDetails;
import uk.org.pentlandscouts.events.model.domain.MedicalDetails;
import uk.org.pentlandscouts.events.service.PersonService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/person/emergency")

public class EmergencyContactController {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyContactController.class);

    @Autowired
    PersonService personService;

    private static final String TABLE_NAME = "MedicalDetails";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";

    private static final String PERSONAL_DETAILS = "MedicalDetails";


    @GetMapping(value = "/update/")
    public ResponseEntity<Object> create(@RequestParam(value = "uid") String uid,
                                         @RequestParam(value = "emergencyContactName") String emergencyContactName,
                                         @RequestParam(value = "emergencyContactNo") String emergencyContactNo,
                                         @RequestParam(value = "emergencyRelationship") String emergencyRelationship) {
        try {


            Person result = null;
            //Lookup the DB for an existing record
            List<Person> lookUpPerson = personService.findByUid(uid);
            //If record is found then add medical details
            if (lookUpPerson.get(0) != null) {
                //Person found add medical details

                Person person = lookUpPerson.get(0);
                person.setEmergencyContactName(emergencyContactName);
                person.setEmergencyContactNo(emergencyContactNo);
                person.setEmergencyRelationship(emergencyRelationship);
                logger.info("Updating the record: {}", person);


                result = personService.update(person);

                Map<String, Person> response = new HashMap<>(1);
                response.put(TABLE_NAME, result);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.info("Person not found for UID: {}", uid);
                Map<String, List<String>> response = new HashMap<>(1);
                List<String> errors = new ArrayList<>();
                errors.add("Person not found for UID: "+uid);
                response.put(ERROR_TITLE, errors);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{uid}")
    public ResponseEntity<Object> getMedicalDetails(@PathVariable("uid") String uid) throws PersonNotFoundException
    {
        Map<String, List<EmergencyContactDetails>> response = new HashMap<>(1);

        try {
            if (!uid.isEmpty()) {
                List<Person> personList = personService.findByUid(uid);
                if (personList.size() > 0 && personList.get(0) == null) {
                    throw new PersonNotFoundException(uid);
                }

                //Convert Person to JSON
                Person p = personList.get(0);
                EmergencyContactDetails emergencyContactDetails = new EmergencyContactDetails();
                emergencyContactDetails.setUid(p.getUid());
                emergencyContactDetails.setFirstName(p.getFirstName());
                emergencyContactDetails.setLastName(p.getLastName());
                emergencyContactDetails.setDob(p.getDob());
                emergencyContactDetails.setEmergencyContactName(p.getEmergencyContactName());
                emergencyContactDetails.setEmergencyContactNo(p.getEmergencyContactNo());
                emergencyContactDetails.setEmergencyContactRelationship(p.getEmergencyRelationship());

                List<EmergencyContactDetails> details = new ArrayList<>();
                details.add(emergencyContactDetails);

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
