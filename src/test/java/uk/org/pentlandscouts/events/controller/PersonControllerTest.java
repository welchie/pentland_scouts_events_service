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

    @MockBean
    private uk.org.pentlandscouts.events.service.EventAttendeeService eventAttendeeService;

    @Test
    public void testGetPersonaldetailsSuccess() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.findByUid("person-1")).thenReturn(Collections.singletonList(person));

        mockMvc.perform(get("/person/personaldetails/person-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person[0].uid").value("person-1"));
    }

    @Test
    public void testFindAllBySubCampAll() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");
        uk.org.pentlandscouts.events.model.EventAttendee attendee = new uk.org.pentlandscouts.events.model.EventAttendee("event-1", "person-1", "true");

        when(service.findAll()).thenReturn(Collections.singletonList(person));
        when(eventAttendeeService.findByPersonUid("person-1")).thenReturn(Collections.singletonList(attendee));

        mockMvc.perform(get("/person/all/All"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person[0].uid").value("person-1"));
    }

    @Test
    public void testFindAllBySubCampSpecific() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");
        uk.org.pentlandscouts.events.model.EventAttendee attendee = new uk.org.pentlandscouts.events.model.EventAttendee("event-1", "person-1", "true");

        when(service.findAllBySubCamp("CampA")).thenReturn(Collections.singletonList(person));
        when(eventAttendeeService.findByPersonUid("person-1")).thenReturn(Collections.singletonList(attendee));

        mockMvc.perform(get("/person/all/CampA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person[0].uid").value("person-1"));
    }

    @Test
    public void testFindByFirstNameLastName() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.findByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.singletonList(person));

        mockMvc.perform(get("/person/find")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person[0].uid").value("person-1"));
    }

    @Test
    public void testCreatePersonaldetails() throws Exception {
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setUid("person-1");

        when(service.findByFirstNameAndLastNameAndDob("John", "Doe", "01/01/2010")).thenReturn(Collections.emptyList());
        when(service.createRecord(any(Person.class))).thenReturn(person);

        mockMvc.perform(get("/person/create/personaldetails")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("dob", "01/01/2010")
                .param("scoutSection", "Scouts")
                .param("sectionName", "Troop")
                .param("scoutGroup", "Group")
                .param("position", "Leader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Person.uid").value("person-1"));
    }

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

    @Test
    public void testGetPersonNotFound() throws Exception {
        when(service.findByUid("person-1")).thenReturn(Collections.singletonList(null));

        mockMvc.perform(get("/person/personaldetails/person-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPersonException() throws Exception {
        when(service.findByUid("person-1")).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(get("/person/personaldetails/person-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testFindAllNotFound() throws Exception {
        when(service.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/person/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindAllException() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(get("/person/all"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testFindAllBySubCampNotFound() throws Exception {
        when(service.findAllBySubCamp("CampA")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/person/all/CampA"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindAllBySubCampException() throws Exception {
        when(service.findAllBySubCamp("CampA")).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(get("/person/all/CampA"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testFindByFirstNameLastNameNotFound() throws Exception {
        when(service.findByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/person/find")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindByFirstNameLastNameException() throws Exception {
        when(service.findByFirstNameAndLastName("John", "Doe")).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(get("/person/find")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testFindByUidException() throws Exception {
        when(service.findByUid("person-1")).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(get("/person/find/person-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreatePersonaldetailsException() throws Exception {
        when(service.findByFirstNameAndLastNameAndDob("John", "Doe", "01/01/2010")).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(get("/person/create/personaldetails")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("dob", "01/01/2010")
                .param("scoutSection", "Scouts")
                .param("sectionName", "Troop")
                .param("scoutGroup", "Group")
                .param("position", "Leader"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreatePersonException() throws Exception {
        when(service.findByFirstNameAndLastNameAndDob(any(), any(), any())).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(post("/person/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"dob\":\"01/01/2010\",\"sortKey\":\"JohnDoe2010\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUpdatePersonNotFound() throws Exception {
        when(service.findByUid("person-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/person/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uid\":\"person-1\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"dob\":\"01/01/2010\"}"))
                .andExpect(status().isOk()); // Returns the input person
    }

    @Test
    public void testRemovePersonException() throws Exception {
        when(service.findByUid("person-1")).thenThrow(new RuntimeException("database error"));

        mockMvc.perform(delete("/person/person-1"))
                .andExpect(status().isInternalServerError());
    }
}
