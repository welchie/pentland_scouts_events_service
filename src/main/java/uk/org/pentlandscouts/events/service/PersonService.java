package uk.org.pentlandscouts.events.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.org.pentlandscouts.events.exception.PersonException;
import uk.org.pentlandscouts.events.exception.PersonNotFoundException;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.repositories.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Crud event for Person entity
 */
@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    PersonRepository personRepo;

    public List<Person> findAll()
    {
        logger.info("Returning all Person objects");
        List<Person> results = personRepo.findAll();

        return results.stream().sorted().collect(Collectors.toList());
    }

    public List<Person> findAllBySubCamp(String subCamp)
    {
        logger.info("Returning all Person objects for subcamp: {}", subCamp);
        List<Person> results = personRepo.findAll().stream().filter(p -> p.getSubCamp().equals(subCamp)).collect(Collectors.toUnmodifiableList());

        return results.stream().sorted().collect(Collectors.toList());
    }

    public List<Person> findByUid(String uid)
    {
        logger.info("Finding Person by id:{}",uid);

        List<Person> results = personRepo.findByUid(uid);
        return results;
    }

    public List<Person> findByFirstNameAndLastName(String firstName, String lastName)
    {
        logger.info("Find Person by:{} and {}",firstName,lastName);
        return personRepo.findByFirstNameAndLastName(firstName,lastName);
    }

    public List<Person> findByFirstNameAndLastNameAndDob(String firstName, String lastName, String dob)
    {
        logger.info("Find Person by:{} , {}, and {}",firstName,lastName,dob);
        return personRepo.findByFirstNameAndLastNameAndDob(firstName,lastName,dob);
    }

    public Person createRecord(Person person) throws PersonException {

        if (person.getUid() == null || person.getUid().trim().equals("")||
                person.getSortKey()== null || person.getSortKey().trim().equals(""))
        {
            throw new PersonException("Person Uid, and Sortkey must be entered to create data");
        }
        else
        {
            //Check if the record already exists, if so perform an update
            List<Person> lookUpPerson = personRepo.findByFirstNameAndLastNameAndDob(person.getFirstName(), person.getLastName(), person.getDob());
            if (lookUpPerson.size() > 0) {
                //Record found set Person.uid
                logger.info("Person exists updating Person record: {}",person);
                person.setUid(lookUpPerson.get(0).getUid());
            }
            else {
                logger.info("Creating new Person record: {}",person);
            }

            return personRepo.save(person);
        }



    }

    public Person update(Person person) throws PersonException {
        if (person.getUid() == null|| person.getFirstName().trim().equals("")||
                person.getLastName()== null || person.getLastName().trim().equals("")||
                person.getDob()== null || person.getDob().trim().equals(""))
        {
            throw new PersonException("Unable to update mandatory values are empty. ID, FirstName, LastName, Dob");
        }
        else
        {
            logger.info("Updating the Person record: {}" , person);
            return personRepo.save(person);
        }
    }

    public void delete(Person person) throws PersonException, PersonNotFoundException
    {
        if (person.getUid() == null|| person.getFirstName().trim().equals(""))
        {
            throw new PersonException("Person UID must be provided. Unable to remove");
        }
        else
        {
            logger.info("Removing  the Person record: {}" , person);
            List<Person> result = personRepo.findByUid(person.getUid());
            if (!result.isEmpty() && result.get(0).getUid().equals(person.getUid())) {
                personRepo.delete(person);
            }
            else
            {
                logger.info("Person with UID: {} not found" , person.getUid());
                throw new PersonNotFoundException("Person with UID: " + person.getUid() + " not found");
            }
        }
    }

}
