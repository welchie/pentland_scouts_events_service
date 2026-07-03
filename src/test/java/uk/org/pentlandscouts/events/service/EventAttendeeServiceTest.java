package uk.org.pentlandscouts.events.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.EventAttendeeNotFoundException;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.repositories.EventAttendeeRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventAttendeeServiceTest {

    @Mock
    private EventAttendeeRepository repo;

    @InjectMocks
    private EventAttendeeService service;

    @Test
    public void testFindAll() {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        when(repo.findAll()).thenReturn(Collections.singletonList(ea));

        List<EventAttendee> result = service.findAll();
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByUid() {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        when(repo.findByUid("uid-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendee> result = service.findByUid("uid-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByPersonUid() {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        when(repo.findByPersonUid("person-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendee> result = service.findByPersonUid("person-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByEventUid() {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        when(repo.findByEventUid("event-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendee> result = service.findByEventUid("event-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByEventUidAndPersonUid() {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        when(repo.findByEventUidAndPersonUid("event-1", "person-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendee> result = service.findByEventUidAndPersonUid("event-1", "person-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByEventUidAndCheckedIn() {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        when(repo.findByEventUidAndCheckedIn("event-1", "true")).thenReturn(Collections.singletonList(ea));

        List<EventAttendee> result = service.findByEventUidAndCheckedIn("event-1", "true");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testCreateRecordInvalid() {
        EventAttendee ea = new EventAttendee();
        assertThrows(EventAttendeeException.class, () -> service.createRecord(ea));
    }

    @Test
    public void testCreateRecordExisting() throws Exception {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        ea.setUid("attendee-1");

        EventAttendee existing = new EventAttendee("event-1", "person-1", "true");
        existing.setUid("existing-uid");

        when(repo.findByEventUidAndPersonUid("event-1", "person-1")).thenReturn(Collections.singletonList(existing));
        when(repo.save(ea)).thenReturn(ea);

        EventAttendee result = service.createRecord(ea);
        assertEquals("existing-uid", result.getUid());
    }

    @Test
    public void testCreateRecordNew() throws Exception {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        ea.setUid("attendee-1");

        when(repo.findByEventUidAndPersonUid("event-1", "person-1")).thenReturn(Collections.emptyList());
        when(repo.save(ea)).thenReturn(ea);

        EventAttendee result = service.createRecord(ea);
        assertEquals("attendee-1", result.getUid());
    }

    @Test
    public void testDeleteInvalid() {
        EventAttendee ea = new EventAttendee();
        assertThrows(EventAttendeeException.class, () -> service.delete(ea));
    }

    @Test
    public void testDeleteNotFound() {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        ea.setUid("attendee-1");

        when(repo.findByUid("attendee-1")).thenReturn(Collections.emptyList());

        assertThrows(EventAttendeeNotFoundException.class, () -> service.delete(ea));
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        ea.setUid("attendee-1");

        when(repo.findByUid("attendee-1")).thenReturn(Collections.singletonList(ea));

        service.delete(ea);
        verify(repo).delete(ea);
    }

    @Test
    public void testUpdateInvalid() {
        EventAttendee ea = new EventAttendee();
        assertThrows(EventAttendeeException.class, () -> service.update(ea));
    }

    @Test
    public void testUpdateValid() throws Exception {
        EventAttendee ea = new EventAttendee("event-1", "person-1", "true");
        ea.setUid("attendee-1");

        when(repo.save(ea)).thenReturn(ea);

        EventAttendee result = service.update(ea);
        assertEquals(ea, result);
    }
}
