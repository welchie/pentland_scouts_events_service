package uk.org.pentlandscouts.events.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.service.PersonService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmergencyContactController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmergencyContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AwsProperties awsProperties;

    @MockBean
    private DynamoDbClient dynamoDbClient;

    @MockBean
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @MockBean
    private DynamoDbTemplate dynamoDbTemplate;

    @MockBean
    private DynamoDbTableNameResolver dynamoDbTableNameResolver;

    @MockBean
    private PersonService personService;

    @Test
    public void testGetEmergencyContactSuccess() throws Exception {
        Person person = new Person("John", "Doe", "dob", "key");
        person.setUid("person-1");
        person.setEmergencyContactName("Name");
        person.setEmergencyContactNo("No");
        person.setEmergencyRelationship("Relation");

        when(personService.findByUid("person-1")).thenReturn(Collections.singletonList(person));

        mockMvc.perform(get("/person/emergency/person-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MedicalDetails[0].emergencyContactName").value("Name"));
    }

    @Test
    public void testUpdateEmergencyContactSuccess() throws Exception {
        Person person = new Person("John", "Doe", "dob", "key");
        person.setUid("person-1");

        when(personService.findByUid("person-1")).thenReturn(Collections.singletonList(person));
        when(personService.update(any(Person.class))).thenReturn(person);

        mockMvc.perform(get("/person/emergency/update/")
                .param("uid", "person-1")
                .param("emergencyContactName", "Name")
                .param("emergencyContactNo", "123")
                .param("emergencyRelationship", "Friend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MedicalDetails.uid").value("person-1"));
    }

    @Test
    public void testUpdateEmergencyContactNotFound() throws Exception {
        when(personService.findByUid("person-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/person/emergency/update/")
                .param("uid", "person-1")
                .param("emergencyContactName", "Name")
                .param("emergencyContactNo", "123")
                .param("emergencyRelationship", "Friend"))
                .andExpect(status().isNotFound());
    }
}
