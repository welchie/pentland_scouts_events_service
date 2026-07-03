package uk.org.pentlandscouts.events.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonalDetailsTest {

    @Test
    public void testGettersSettersAndConstructors() {
        PersonalDetails details = new PersonalDetails();
        details.setUid("uid-1");
        details.setFirstName("John");
        details.setLastName("Doe");
        details.setDob("01/01/2010");
        details.setScoutSection("Scouts");
        details.setSectionName("A");
        details.setScoutGroup("Group 1");
        details.setPosition("Leader");

        assertEquals("uid-1", details.getUid());
        assertEquals("John", details.getFirstName());
        assertEquals("Doe", details.getLastName());
        assertEquals("01/01/2010", details.getDob());
        assertEquals("Scouts", details.getScoutSection());
        assertEquals("A", details.getSectionName());
        assertEquals("Group 1", details.getScoutGroup());
        assertEquals("Leader", details.getPosition());
    }

    @Test
    public void testEqualsAndHashCode() {
        PersonalDetails details1 = new PersonalDetails();
        details1.setUid("uid-1");
        details1.setFirstName("John");
        details1.setLastName("Doe");

        PersonalDetails details2 = new PersonalDetails();
        details2.setUid("uid-1");
        details2.setFirstName("John");
        details2.setLastName("Doe");

        PersonalDetails details3 = new PersonalDetails();
        details3.setUid("uid-2");

        assertEquals(details1, details2);
        assertEquals(details1.hashCode(), details2.hashCode());
        assertNotEquals(details1, details3);
        assertNotEquals(details1, null);
        assertNotEquals(details1, "string-object");
    }

    @Test
    public void testToString() {
        PersonalDetails details = new PersonalDetails();
        details.setUid("uid-1");
        details.setFirstName("John");
        
        String stringRepresentation = details.toString();
        assertTrue(stringRepresentation.contains("uid='uid-1'"));
        assertTrue(stringRepresentation.contains("firstName='John'"));
    }
}
