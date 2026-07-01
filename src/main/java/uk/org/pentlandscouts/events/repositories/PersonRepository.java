package uk.org.pentlandscouts.events.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.PersonId;

import java.util.List;
import java.util.Objects;

@Repository
public class PersonRepository extends AbstractDynamoDbRepository<Person, PersonId> {

    @Autowired
    public PersonRepository(DynamoDbTemplate dynamoDbTemplate, DynamoDbEnhancedClient enhancedClient, AwsProperties awsProperties) {
        super(dynamoDbTemplate, enhancedClient, Person.class,
              id -> Key.builder().partitionValue(id.getUid()).sortValue(id.getSortKey()).build(),
              (awsProperties.getTablePrefix() != null ? awsProperties.getTablePrefix() : "") + "Person");
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
