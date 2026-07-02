package uk.org.pentlandscouts.events.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.service.PersonService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService service;

    @Test
    public void testFindAll() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.findAll()).thenReturn(Collections.singletonList(person));

        mockMvc.perform(get("/person/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person[0].uid").value("person-1"));
    }

    @Test
    public void testFindByUidSuccess() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.findByUid("person-1")).thenReturn(Collections.singletonList(person));

        mockMvc.perform(get("/person/find/person-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person[0].uid").value("person-1"));
    }

    @Test
    public void testFindByUidNotFound() throws Exception {
        when(service.findByUid("person-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/person/find/person-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePersonSuccess() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.createRecord(any(Person.class))).thenReturn(person);

        mockMvc.perform(post("/person/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"dob\":\"01/01/2010\",\"sortKey\":\"JohnDoe2010\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person.uid").value("person-1"));
    }

    @Test
    public void testUpdatePersonSuccess() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.findByUid("person-1")).thenReturn(Collections.singletonList(person));
        when(service.update(any(Person.class))).thenReturn(person);

        mockMvc.perform(post("/person/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uid\":\"person-1\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"dob\":\"01/01/2010\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("person-1"));
    }

    @Test
    public void testRemovePersonSuccess() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.findByUid("person-1")).thenReturn(Collections.singletonList(person));

        mockMvc.perform(delete("/person/person-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person[0]").value("Person with UID: person-1 removed"));
    }

    @Test
    public void testRemovePersonNotFound() throws Exception {
        when(service.findByUid("person-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(delete("/person/person-1"))
                .andExpect(status().isNotFound());
    }
}
