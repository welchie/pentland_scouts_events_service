package uk.org.pentlandscouts.events.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.service.EventService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService service;

    @Test
    public void testFindAll() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(service.findAll()).thenReturn(Collections.singletonList(event));

        mockMvc.perform(get("/event/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Event[0].uid").value("event-1"));
    }

    @Test
    public void testFindByUidSuccess() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(service.findByUid("event-1")).thenReturn(Collections.singletonList(event));

        mockMvc.perform(get("/event/find/event-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Event[0].uid").value("event-1"));
    }

    @Test
    public void testFindByUidNotFound() throws Exception {
        when(service.findByUid("event-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/event/find/event-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateEventSuccess() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(service.createRecord(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/event/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Camp\",\"venue\":\"Venue\",\"startDate\":\"01/01/2026\",\"endDate\":\"02/01/2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Event.uid").value("event-1"));
    }

    @Test
    public void testUpdateEventSuccess() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(service.findByUid("event-1")).thenReturn(Collections.singletonList(event));
        when(service.update(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/event/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uid\":\"event-1\",\"name\":\"Camp\",\"venue\":\"Venue\",\"startDate\":\"01/01/2026\",\"endDate\":\"02/01/2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("event-1"));
    }

    @Test
    public void testRemoveEventSuccess() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(service.findByUid("event-1")).thenReturn(Collections.singletonList(event));

        mockMvc.perform(delete("/event/event-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Event[0]").value("Event with UID: event-1 removed"));
    }

    @Test
    public void testRemoveEventNotFound() throws Exception {
        when(service.findByUid("event-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(delete("/event/event-1"))
                .andExpect(status().isNotFound());
    }
}
