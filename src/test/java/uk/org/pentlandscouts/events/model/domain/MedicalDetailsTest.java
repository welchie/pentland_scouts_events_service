package uk.org.pentlandscouts.events.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MedicalDetailsTest {

    @Test
    public void testGettersSettersAndConstructors() {
        MedicalDetails details = new MedicalDetails();
        details.setUid("uid-1");
        details.setFirstName("John");
        details.setLastName("Doe");
        details.setDob("01/01/2010");
        details.setMedicine("Aspirin");
        details.setAllergies("Nuts");

        assertEquals("uid-1", details.getUid());
        assertEquals("John", details.getFirstName());
        assertEquals("Doe", details.getLastName());
        assertEquals("01/01/2010", details.getDob());
        assertEquals("Aspirin", details.getMedicine());
        assertEquals("Nuts", details.getAllergies());
    }

    @Test
    public void testEqualsAndHashCode() {
        MedicalDetails details1 = new MedicalDetails();
        details1.setUid("uid-1");
        details1.setFirstName("John");

        MedicalDetails details2 = new MedicalDetails();
        details2.setUid("uid-1");
        details2.setFirstName("John");

        MedicalDetails details3 = new MedicalDetails();
        details3.setUid("uid-2");

        assertEquals(details1, details2);
        assertEquals(details1.hashCode(), details2.hashCode());
        assertNotEquals(details1, details3);
        assertNotEquals(details1, null);
        assertNotEquals(details1, "string-object");
    }

    @Test
    public void testToString() {
        MedicalDetails details = new MedicalDetails();
        details.setUid("uid-1");
        details.setFirstName("John");

        String stringRepresentation = details.toString();
        assertTrue(stringRepresentation.contains("uid='uid-1'"));
        assertTrue(stringRepresentation.contains("firstName='John'"));
    }
}
