package uk.org.pentlandscouts.events.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmergencyContactDetailsTest {

    @Test
    public void testGettersSettersAndConstructors() {
        EmergencyContactDetails details = new EmergencyContactDetails();
        details.setUid("uid-1");
        details.setFirstName("John");
        details.setLastName("Doe");
        details.setDob("01/01/2010");
        details.setEmergencyContactName("Jane Doe");
        details.setEmergencyContactNo("07123456789");
        details.setEmergencyContactRelationship("Mother");

        assertEquals("uid-1", details.getUid());
        assertEquals("John", details.getFirstName());
        assertEquals("Doe", details.getLastName());
        assertEquals("01/01/2010", details.getDob());
        assertEquals("Jane Doe", details.getEmergencyContactName());
        assertEquals("07123456789", details.getEmergencyContactNo());
        assertEquals("Mother", details.getEmergencyContactRelationship());
    }

    @Test
    public void testEqualsAndHashCode() {
        EmergencyContactDetails details1 = new EmergencyContactDetails();
        details1.setUid("uid-1");
        details1.setFirstName("John");

        EmergencyContactDetails details2 = new EmergencyContactDetails();
        details2.setUid("uid-1");
        details2.setFirstName("John");

        EmergencyContactDetails details3 = new EmergencyContactDetails();
        details3.setUid("uid-2");

        assertEquals(details1, details2);
        assertEquals(details1.hashCode(), details2.hashCode());
        assertNotEquals(details1, details3);
        assertNotEquals(details1, null);
        assertNotEquals(details1, "string-object");
    }

    @Test
    public void testToString() {
        EmergencyContactDetails details = new EmergencyContactDetails();
        details.setUid("uid-1");
        details.setFirstName("John");

        String stringRepresentation = details.toString();
        assertTrue(stringRepresentation.contains("uid='uid-1'"));
        assertTrue(stringRepresentation.contains("firstName='John'"));
    }
}
