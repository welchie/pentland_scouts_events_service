package uk.org.pentlandscouts.events.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.pentlandscouts.events.exception.EventException;
import uk.org.pentlandscouts.events.exception.EventNotFoundException;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.repositories.EventRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository repo;

    @InjectMocks
    private EventService service;

    @Test
    public void testFindAll() {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        when(repo.findAll()).thenReturn(Collections.singletonList(event));

        List<Event> result = service.findAll();
        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
    }

    @Test
    public void testFindByUid() {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        when(repo.findByUid("uid-1")).thenReturn(Collections.singletonList(event));

        List<Event> result = service.findByUid("uid-1");
        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
    }

    @Test
    public void testFindByNameAndVenue() {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        when(repo.findByNameAndVenue("Camp", "Venue")).thenReturn(Collections.singletonList(event));

        List<Event> result = service.findByNameAndVenue("Camp", "Venue");
        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
    }

    @Test
    public void testCreateRecordInvalid() {
        Event event = new Event();
        assertThrows(EventException.class, () -> service.createRecord(event));
    }

    @Test
    public void testCreateRecordExisting() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        Event existing = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        existing.setUid("existing-uid");

        when(repo.findByNameAndVenue("Camp", "Venue")).thenReturn(Collections.singletonList(existing));
        when(repo.save(event)).thenReturn(event);

        Event result = service.createRecord(event);
        assertEquals("existing-uid", result.getUid());
    }

    @Test
    public void testCreateRecordNew() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(repo.findByNameAndVenue("Camp", "Venue")).thenReturn(Collections.emptyList());
        when(repo.save(event)).thenReturn(event);

        Event result = service.createRecord(event);
        assertEquals("event-1", result.getUid());
    }

    @Test
    public void testUpdateInvalid() {
        Event event = new Event();
        assertThrows(EventException.class, () -> service.update(event));
    }

    @Test
    public void testUpdateValid() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(repo.save(event)).thenReturn(event);

        Event result = service.update(event);
        assertEquals(event, result);
    }

    @Test
    public void testDeleteInvalid() {
        Event event = new Event();
        assertThrows(EventException.class, () -> service.delete(event));
    }

    @Test
    public void testDeleteNotFound() {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(repo.findByUid("event-1")).thenReturn(Collections.emptyList());

        assertThrows(EventNotFoundException.class, () -> service.delete(event));
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        Event event = new Event("Camp", "Venue", "01/01/2026", "02/01/2026");
        event.setUid("event-1");

        when(repo.findByUid("event-1")).thenReturn(Collections.singletonList(event));

        service.delete(event);
        verify(repo).delete(event);
    }
}
