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

@WebMvcTest(MedicalDetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MedicalDetailsControllerTest {

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
    public void testGetMedicalDetailsSuccess() throws Exception {
        Person person = new Person("John", "Doe", "dob", "key");
        person.setUid("person-1");
        person.setMedicine("aspirin");
        person.setAllergies("peanuts");

        when(personService.findByUid("person-1")).thenReturn(Collections.singletonList(person));

        mockMvc.perform(get("/person/medicaldetails/person-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MedicalDetails[0].medicine").value("aspirin"));
    }

    @Test
    public void testUpdateMedicalDetailsSuccess() throws Exception {
        Person person = new Person("John", "Doe", "dob", "key");
        person.setUid("person-1");

        when(personService.findByUid("person-1")).thenReturn(Collections.singletonList(person));
        when(personService.update(any(Person.class))).thenReturn(person);

        mockMvc.perform(get("/person/medicaldetails/update")
                .param("uid", "person-1")
                .param("medicine", "aspirin")
                .param("allergies", "peanuts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MedicalDetails.uid").value("person-1"));
    }

    @Test
    public void testUpdateMedicalDetailsNotFound() throws Exception {
        when(personService.findByUid("person-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/person/medicaldetails/update")
                .param("uid", "person-1")
                .param("medicine", "aspirin")
                .param("allergies", "peanuts"))
                .andExpect(status().isNotFound());
    }
}
