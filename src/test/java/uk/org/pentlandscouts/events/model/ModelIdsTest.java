package uk.org.pentlandscouts.events.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModelIdsTest {

    @Test
    public void testPersonId() {
        PersonId id = new PersonId("uid", "key");
        assertEquals("uid", id.getUid());
        assertEquals("key", id.getSortKey());

        PersonId defaultId = new PersonId();
        assertNull(defaultId.getUid());
        assertNull(defaultId.getSortKey());

        defaultId.setUid("uid2");
        defaultId.setSortKey("key2");
        assertEquals("uid2", defaultId.getUid());
        assertEquals("key2", defaultId.getSortKey());
    }

    @Test
    public void testEventId() {
        EventId id = new EventId("uid", "key");
        assertEquals("uid", id.getUid());
        assertEquals("key", id.getSortKey());

        EventId defaultId = new EventId();
        assertNull(defaultId.getUid());
        assertNull(defaultId.getSortKey());

        defaultId.setUid("uid2");
        defaultId.setSortKey("key2");
        assertEquals("uid2", defaultId.getUid());
        assertEquals("key2", defaultId.getSortKey());
    }

    @Test
    public void testEventAttendeeId() {
        EventAttendeeId id = new EventAttendeeId("uid", "key");
        assertEquals("uid", id.getUid());
        assertEquals("key", id.getSortKey());

        EventAttendeeId defaultId = new EventAttendeeId();
        assertNull(defaultId.getUid());
        assertNull(defaultId.getSortKey());

        defaultId.setUid("uid2");
        defaultId.setSortKey("key2");
        assertEquals("uid2", defaultId.getUid());
        assertEquals("key2", defaultId.getSortKey());
    }
}
