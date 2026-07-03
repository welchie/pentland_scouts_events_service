package uk.org.pentlandscouts.events.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonDomainTest {

    @Test
    public void testGettersSettersAndConstructors() {
        PersonDomain person = new PersonDomain("John", "Doe", "01/01/2010", "JohnDoe2010", "person-1");

        assertEquals("person-1", person.getUid());
        assertEquals("JohnDoe2010", person.getSortKey());
        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
        assertEquals("01/01/2010", person.getDob());

        person.setScoutSection("Scouts");
        person.setSectionName("A");
        person.setScoutGroup("Group 1");
        person.setPosition("Leader");
        person.setMedicine("Aspirin");
        person.setAllergies("Nuts");
        person.setDietary("Vegan");
        person.setContactEmail("john@example.com");
        person.setContactPhoneNo("07123456789");
        person.setEmergencyContactName("Jane Doe");
        person.setEmergencyContactNo("07987654321");
        person.setEmergencyRelationship("Mother");
        person.setPhotoPermission("true");
        person.setSubCamp("Camp A");
        person.setCheckedIn("true");
        person.setUrl("http://example.com/person/person-1");
        person.setLastUpdated("2026-07-02");

        assertEquals("Scouts", person.getScoutSection());
        assertEquals("A", person.getSectionName());
        assertEquals("Group 1", person.getScoutGroup());
        assertEquals("Leader", person.getPosition());
        assertEquals("Aspirin", person.getMedicine());
        assertEquals("Nuts", person.getAllergies());
        assertEquals("Vegan", person.getDietary());
        assertEquals("john@example.com", person.getContactEmail());
        assertEquals("07123456789", person.getContactPhoneNo());
        assertEquals("Jane Doe", person.getEmergencyContactName());
        assertEquals("07987654321", person.getEmergencyContactNo());
        assertEquals("Mother", person.getEmergencyRelationship());
        assertEquals("true", person.getPhotoPermission());
        assertEquals("Camp A", person.getSubCamp());
        assertEquals("true", person.getCheckedIn());
        assertEquals("http://example.com/person/person-1", person.getUrl());
        assertEquals("2026-07-02", person.getLastUpdated());
    }

    @Test
    public void testEqualsAndHashCode() {
        PersonDomain person1 = new PersonDomain("John", "Doe", "01/01/2010", "JohnDoe2010", "person-1");
        PersonDomain person2 = new PersonDomain("John", "Doe", "01/01/2010", "JohnDoe2010", "person-1");
        PersonDomain person3 = new PersonDomain("John", "Doe", "01/01/2010", "JohnDoe2010-other", "person-2");

        assertEquals(person1, person2);
        assertEquals(person1.hashCode(), person2.hashCode());
        assertNotEquals(person1, person3);
        assertNotEquals(person1, null);
        assertNotEquals(person1, "string-object");
    }

    @Test
    public void testCompareTo() {
        PersonDomain person1 = new PersonDomain("John", "Doe", "01/01/2010", "JohnDoe2010", "person-1");
        PersonDomain person2 = new PersonDomain("Jane", "Doe", "01/01/2010", "JaneDoe2010", "person-2");

        assertTrue(person1.compareTo(person2) > 0); // "JohnDoe2010" > "JaneDoe2010"
        assertTrue(person2.compareTo(person1) < 0);
        assertEquals(0, person1.compareTo(person1));
    }

    @Test
    public void testToString() {
        PersonDomain person = new PersonDomain("John", "Doe", "01/01/2010", "JohnDoe2010", "person-1");
        String str = person.toString();
        assertTrue(str.contains("uid='person-1'"));
        assertTrue(str.contains("sortKey='JohnDoe2010'"));
    }
}
