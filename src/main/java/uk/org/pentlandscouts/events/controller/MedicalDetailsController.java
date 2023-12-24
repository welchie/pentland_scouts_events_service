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
import uk.org.pentlandscouts.events.service.PersonService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/person")

public class MedicalDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(MedicalDetailsController.class);

    @Autowired
    PersonService personService;

    private static final String TABLE_NAME = "Person";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";

    private static final String PERSONAL_DETAILS = "MedicalDetails";


    @GetMapping(value = "/create/medicaldetails")
    public ResponseEntity<Object> create(@RequestParam(value = "uid") String uid,
                                         @RequestParam(value = "medicine") String medicine,
                                         @RequestParam(value = "allergies") String allergies) {
        try {


            Person result = null;
            //Lookup the DB for an existing record
            List<Person> lookUpPerson = personService.findByUid(uid);
            //If record is found then add medical details
            if (lookUpPerson.size() >0) {
                //Person not found create new record


                Person person = lookUpPerson.get(0);
                //person.setSortKey(PERSONAL_DETAILS);
                person.setMedicine(medicine);
                person.setAllergies(allergies);
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
}
