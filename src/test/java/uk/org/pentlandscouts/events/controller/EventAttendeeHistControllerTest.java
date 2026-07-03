package uk.org.pentlandscouts.events.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.service.EventAttendeeHistService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventAttendeeHistController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventAttendeeHistControllerTest {

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
    private EventAttendeeHistService service;

    @Test
    public void testFindAllEmpty() throws Exception {
        when(service.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/eventattendeehist/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindAllSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        when(service.findAll()).thenReturn(Collections.singletonList(hist));

        mockMvc.perform(get("/eventattendeehist/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendeeHist[0].eventUid").value("event-1"));
    }

    @Test
    public void testFindByEventUidEmpty() throws Exception {
        when(service.findByEventUid("event-1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/eventattendeehist/findbyevent").param("eventUid", "event-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindByEventUidSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        when(service.findByEventUid("event-1")).thenReturn(Collections.singletonList(hist));

        mockMvc.perform(get("/eventattendeehist/findbyevent").param("eventUid", "event-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendeeHist[0].eventUid").value("event-1"));
    }

    @Test
    public void testFindByPersonUidSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        when(service.findByPersonUid("person-1")).thenReturn(Collections.singletonList(hist));

        mockMvc.perform(get("/eventattendeehist/findbyperson").param("personuid", "person-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendeeHist[0].personUid").value("person-1"));
    }

    @Test
    public void testFindByEventUidAndCheckedInSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        when(service.findByEventUidAndCheckedIn("event-1", "true")).thenReturn(Collections.singletonList(hist));

        mockMvc.perform(get("/eventattendeehist/find")
                .param("eventUid", "event-1")
                .param("checkedIn", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendeeHist[0].eventUid").value("event-1"));
    }

    @Test
    public void testCreateSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        when(service.findByEventUidAndPersonUid("event-1", "person-1")).thenReturn(Collections.emptyList());
        when(service.createRecord(any(EventAttendeeHist.class))).thenReturn(hist);

        mockMvc.perform(post("/eventattendeehist/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"eventUid\":\"event-1\",\"personUid\":\"person-1\",\"photoPermission\":\"true\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EventAttendeeHist.eventUid").value("event-1"));
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        hist.setUid("hist-1");
        when(service.findByUid("hist-1")).thenReturn(Collections.singletonList(hist));
        when(service.update(any(EventAttendeeHist.class))).thenReturn(hist);

        mockMvc.perform(post("/eventattendeehist/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uid\":\"hist-1\",\"eventUid\":\"event-1\",\"personUid\":\"person-1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        hist.setUid("hist-1");
        when(service.findByUid("hist-1")).thenReturn(Collections.singletonList(hist));

        mockMvc.perform(delete("/eventattendeehist/hist-1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckinSuccess() throws Exception {
        EventAttendeeHist hist = new EventAttendeeHist("event-1", "person-1", "true");
        when(service.findByEventUidAndPersonUid("event-1", "person-1")).thenReturn(Collections.singletonList(hist));
        when(service.update(any(EventAttendeeHist.class))).thenReturn(hist);

        mockMvc.perform(get("/eventattendeehist/checkin")
                .param("eventUID", "event-1")
                .param("personUID", "person-1")
                .param("checkIn", "true"))
                .andExpect(status().isOk());
    }
}
