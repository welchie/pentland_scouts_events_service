package uk.org.pentlandscouts.events.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.EventAttendeeNotFoundException;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.repositories.EventAttendeeHistRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventAttendeeHistServiceTest {

    @Mock
    private EventAttendeeHistRepository repo;

    @InjectMocks
    private EventAttendeeHistService service;

    @Test
    public void testFindAll() {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        when(repo.findAll()).thenReturn(Collections.singletonList(ea));

        List<EventAttendeeHist> result = service.findAll();
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByUid() {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        when(repo.findByUid("uid-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendeeHist> result = service.findByUid("uid-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByPersonUid() {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        when(repo.findByPersonUid("person-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendeeHist> result = service.findByPersonUid("person-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByEventUid() {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        when(repo.findByEventUid("event-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendeeHist> result = service.findByEventUid("event-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByEventUidAndPersonUid() {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        when(repo.findByEventUidAndPersonUid("event-1", "person-1")).thenReturn(Collections.singletonList(ea));

        List<EventAttendeeHist> result = service.findByEventUidAndPersonUid("event-1", "person-1");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testFindByEventUidAndCheckedIn() {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        when(repo.findByEventUidAndCheckedIn("event-1", "true")).thenReturn(Collections.singletonList(ea));

        List<EventAttendeeHist> result = service.findByEventUidAndCheckedIn("event-1", "true");
        assertEquals(1, result.size());
        assertEquals(ea, result.get(0));
    }

    @Test
    public void testCreateRecordInvalid() {
        EventAttendeeHist ea = new EventAttendeeHist();
        assertThrows(EventAttendeeException.class, () -> service.createRecord(ea));
    }

    @Test
    public void testCreateRecordSuccess() throws Exception {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        ea.setUid("hist-1");

        when(repo.save(ea)).thenReturn(ea);

        EventAttendeeHist result = service.createRecord(ea);
        assertEquals(ea, result);
    }

    @Test
    public void testDeleteInvalid() {
        EventAttendeeHist ea = new EventAttendeeHist();
        assertThrows(EventAttendeeException.class, () -> service.delete(ea));
    }

    @Test
    public void testDeleteNotFound() {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        ea.setUid("hist-1");

        when(repo.findByUid("hist-1")).thenReturn(Collections.emptyList());

        assertThrows(EventAttendeeNotFoundException.class, () -> service.delete(ea));
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        ea.setUid("hist-1");

        when(repo.findByUid("hist-1")).thenReturn(Collections.singletonList(ea));

        service.delete(ea);
        verify(repo).delete(ea);
    }

    @Test
    public void testUpdateInvalid() {
        EventAttendeeHist ea = new EventAttendeeHist();
        assertThrows(EventAttendeeException.class, () -> service.update(ea));
    }

    @Test
    public void testUpdateValid() throws Exception {
        EventAttendeeHist ea = new EventAttendeeHist("event-1", "person-1", "true");
        ea.setUid("hist-1");

        when(repo.save(ea)).thenReturn(ea);

        EventAttendeeHist result = service.update(ea);
        assertEquals(ea, result);
    }
}
