package uk.org.pentlandscouts.events.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventEmergencyContactTest {

    @Test
    public void testConstructorsGettersAndSetters() {
        EventEmergencyContact contact = new EventEmergencyContact(
                "uid-1", "Camp", "Venue", "01/01/2026", "02/01/2026", "07123456789", "Jane Doe"
        );

        assertEquals("uid-1", contact.getUid());
        assertEquals("Camp", contact.getName());
        assertEquals("Venue", contact.getVenue());
        assertEquals("01/01/2026", contact.getStartDate());
        assertEquals("02/01/2026", contact.getEndDate());
        assertEquals("07123456789", contact.getContactNo());
        assertEquals("Jane Doe", contact.getContactName());

        EventEmergencyContact contact2 = new EventEmergencyContact();
        contact2.setUid("uid-2");
        assertEquals("uid-2", contact2.getUid());
    }

    @Test
    public void testToString() {
        EventEmergencyContact contact = new EventEmergencyContact(
                "uid-1", "Camp", "Venue", "01/01/2026", "02/01/2026", "07123456789", "Jane Doe"
        );

        String stringRepresentation = contact.toString();
        assertTrue(stringRepresentation.contains("uid='uid-1'"));
        assertTrue(stringRepresentation.contains("name='Camp'"));
    }
}
