package uk.org.pentlandscouts.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.exception.PersonNotFoundException;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.service.PersonService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/person")

public class PersonController {

    private  static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    PersonService personService;

    private static final String TABLE_NAME = "Person";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPerson(@PathVariable("id") String id) throws PersonNotFoundException {

        Map<String, List<Person>> response = new HashMap<>(1);

        try {
            if (!id.isEmpty()) {
                List<Person> personList = personService.findById(id);
                if (personList.isEmpty()) {
                    throw new PersonNotFoundException(id);
                }
                response.put(TABLE_NAME, personList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        }
        catch (PersonNotFoundException pe) {
            return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);

        }
        catch (Exception e)
        {
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
            @RequestParam(value = "dob") String dob)
    {
        Map<String, List<Person>> response = new HashMap<>(1);
        try {
            if (!firstName.isEmpty() && !lastName.isEmpty() && !dob.isEmpty()) {

                List<Person> personList = personService.findByFirstNameAndLastNameAndDob(firstName,lastName,dob);
                if (personList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, personList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestParam(value = "firstName") String firstName,
                                         @RequestParam(value = "lastName") String lastName,
                                         @RequestParam(value = "dob") String dob)
    {
        try {

            Person result = null;
            //Lookup the DB for an existing record
            List<Person> lookUpPerson = personService.findByFirstNameAndLastNameAndDob(firstName,lastName,dob);
            if (lookUpPerson.size() == 0) {
                //Person not found create new record

                Person person = new Person(firstName, lastName, dob);
                logger.info("Creating new record: {}", person);


                result = personService.createRecord(person);
            }
            else
            {
                result = lookUpPerson.get(0);
            }

            Map<String, Person> response = new HashMap<>(1);
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
