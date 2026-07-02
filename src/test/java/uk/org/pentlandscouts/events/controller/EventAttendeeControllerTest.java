package uk.org.pentlandscouts.events.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.service.EventAttendeeHistService;
import uk.org.pentlandscouts.events.service.EventAttendeeService;
import uk.org.pentlandscouts.events.service.EventService;
import uk.org.pentlandscouts.events.service.PersonService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventAttendeeController.class)
public class EventAttendeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventAttendeeService service;

    @MockBean
    private EventAttendeeHistService histService;

    @MockBean
    private PersonService personService;

    @MockBean
    private EventService eventService;

    @Test
    public void testCheckinExistingAttendee() throws Exception {
        String eventUid = "event-1";
        String personUid = "person-1";
        EventAttendee attendee = new EventAttendee(eventUid, personUid, "true");
        attendee.setUid("attendee-1");

        when(service.findByEventUidAndPersonUid(eventUid, personUid))
                .thenReturn(Collections.singletonList(attendee));
        when(service.update(any(EventAttendee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(get("/eventattendee/checkin")
                .param("eventUID", eventUid)
                .param("personUID", personUid)
                .param("checkIn", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendee.checkedIn").value("true"));
    }

    @Test
    public void testCheckinAutoRegisterSuccess() throws Exception {
        String eventUid = "event-1";
        String personUid = "person-1";

        // No existing attendee
        when(service.findByEventUidAndPersonUid(eventUid, personUid))
                .thenReturn(Collections.emptyList());

        // Event and Person exist
        Person person = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        person.setPhotoPermission("true");
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");

        when(personService.findByUid(personUid)).thenReturn(Collections.singletonList(person));
        when(eventService.findByUid(eventUid)).thenReturn(Collections.singletonList(event));

        when(service.createRecord(any(EventAttendee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(get("/eventattendee/checkin")
                .param("eventUID", eventUid)
                .param("personUID", personUid)
                .param("checkIn", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendee.checkedIn").value("true"));
    }

    @Test
    public void testCheckinAutoRegisterNotFound() throws Exception {
        String eventUid = "event-1";
        String personUid = "person-1";

        when(service.findByEventUidAndPersonUid(eventUid, personUid))
                .thenReturn(Collections.emptyList());
        when(personService.findByUid(personUid)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/eventattendee/checkin")
                .param("eventUID", eventUid)
                .param("personUID", personUid)
                .param("checkIn", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        attendee.setUid("attendee-1");

        when(service.findByUid("attendee-1")).thenReturn(Collections.singletonList(attendee));
        when(service.update(any(EventAttendee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/eventattendee/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uid\":\"attendee-1\",\"eventUid\":\"event-1\",\"personUid\":\"person-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendee.uid").value("attendee-1"));
    }

    @Test
    public void testUpdateNotFound() throws Exception {
        when(service.findByUid("attendee-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/eventattendee/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uid\":\"attendee-1\",\"eventUid\":\"event-1\",\"personUid\":\"person-1\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveSuccess() throws Exception {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        attendee.setUid("attendee-1");

        when(service.findByUid("attendee-1")).thenReturn(Collections.singletonList(attendee));

        mockMvc.perform(delete("/eventattendee/attendee-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendee[0]").value("EventAttend with UID: attendee-1 removed"));
    }

    @Test
    public void testRemoveNotFound() throws Exception {
        when(service.findByUid("attendee-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(delete("/eventattendee/attendee-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindByEventSuccess() throws Exception {
        EventAttendee attendee = new EventAttendee("event-1", "person-1", "true");
        attendee.setUid("attendee-1");

        when(service.findByEventUid("event-1")).thenReturn(Collections.singletonList(attendee));

        mockMvc.perform(get("/eventattendee/findbyevent").param("eventUid", "event-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendee[0].uid").value("attendee-1"));
    }
}
