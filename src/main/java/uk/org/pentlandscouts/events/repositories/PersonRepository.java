package uk.org.pentlandscouts.events.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.PersonId;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Repository
public class PersonRepository {

    @Autowired
    private DynamoDbTemplate dynamoDbTemplate;

    public Person save(Person person) {
        dynamoDbTemplate.save(person);
        return person;
    }

    public <S extends Person> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    public Optional<Person> findById(PersonId personId) {
        if (personId == null || personId.getUid() == null || personId.getSortKey() == null) {
            return Optional.empty();
        }
        Person person = dynamoDbTemplate.load(
                Key.builder()
                        .partitionValue(personId.getUid())
                        .sortValue(personId.getSortKey())
                        .build(),
                Person.class
        );
        return Optional.ofNullable(person);
    }

    public boolean existsById(PersonId personId) {
        return findById(personId).isPresent();
    }

    public List<Person> findAll() {
        return dynamoDbTemplate.scanAll(Person.class).items().stream().toList();
    }

    public Iterable<Person> findAllById(Iterable<PersonId> ids) {
        java.util.List<Person> list = new java.util.ArrayList<>();
        for (PersonId id : ids) {
            findById(id).ifPresent(list::add);
        }
        return list;
    }

    public long count() {
        return findAll().size();
    }

    public void deleteById(PersonId personId) {
        findById(personId).ifPresent(this::delete);
    }

    public void delete(Person person) {
        dynamoDbTemplate.delete(person);
    }

    public void deleteAllById(Iterable<? extends PersonId> ids) {
        for (PersonId id : ids) {
            deleteById(id);
        }
    }

    public void deleteAll(Iterable<? extends Person> entities) {
        for (Person entity : entities) {
            delete(entity);
        }
    }

    public void deleteAll() {
        deleteAll(findAll());
    }

    public List<Person> findByOrderBySortKeyAsc() {
        return dynamoDbTemplate.scanAll(Person.class).items().stream().sorted().toList();
    }

    public List<Person> findByFirstNameAndLastName(String firstName, String lastName) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder()
                                        .partitionValue(lastName)
                                        .sortValue(firstName)
                                        .build()
                        ))
                        .build(),
                Person.class,
                "firstname-lastname-index"
        ).items().stream().toList();
    }

    public List<Person> findByFirstNameAndLastNameAndDob(String firstName, String lastName, String dob) {
        return findByFirstNameAndLastName(firstName, lastName).stream()
                .filter(p -> Objects.equals(p.getDob(), dob))
                .toList();
    }

    public List<Person> findByUid(String uid) {
        return dynamoDbTemplate.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(QueryConditional.keyEqualTo(
                                Key.builder().partitionValue(uid).build()
                        ))
                        .build(),
                Person.class
        ).items().stream().toList();
    }
}
