package uk.org.pentlandscouts.events.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.PersonId;


import java.util.List;

@EnableScan
public interface PersonRepository extends CrudRepository<Person, PersonId> {

    List<Person> findAll();

    List<Person> findByOrderBySortKeyAsc();
    List<Person> findByFirstNameAndLastName(String firstName, String lastName);

    List<Person> findByFirstNameAndLastNameAndDob(String firstName, String lastName,String dob);

    List<Person> findByUid(String uid);

}
