package uk.org.pentlandscouts.events.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.pentlandscouts.events.exception.PersonException;
import uk.org.pentlandscouts.events.exception.PersonNotFoundException;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.repositories.PersonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepo;

    @InjectMocks
    private PersonService service;

    @Test
    public void testFindAll() {
        Person p1 = new Person("B", "Last", "dob", "keyB");
        Person p2 = new Person("A", "Last", "dob", "keyA");
        List<Person> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);

        when(personRepo.findAll()).thenReturn(list);

        List<Person> result = service.findAll();
        assertEquals(2, result.size());
        assertEquals(p2, result.get(0)); // Sorted by comparable (sortKey)
    }

    @Test
    public void testFindAllBySubCamp() {
        Person p1 = new Person("John", "Doe", "dob", "key1");
        p1.setSubCamp("CampA");
        Person p2 = new Person("Jane", "Doe", "dob", "key2");
        p2.setSubCamp("CampB");

        List<Person> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);

        when(personRepo.findAll()).thenReturn(list);

        List<Person> result = service.findAllBySubCamp("CampA");
        assertEquals(1, result.size());
        assertEquals(p1, result.get(0));
    }

    @Test
    public void testFindByUid() {
        Person p = new Person("John", "Doe", "dob", "key1");
        when(personRepo.findByUid("uid-1")).thenReturn(Collections.singletonList(p));

        List<Person> result = service.findByUid("uid-1");
        assertEquals(1, result.size());
        assertEquals(p, result.get(0));
    }

    @Test
    public void testFindByFirstNameAndLastName() {
        Person p = new Person("John", "Doe", "dob", "key1");
        when(personRepo.findByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.singletonList(p));

        List<Person> result = service.findByFirstNameAndLastName("John", "Doe");
        assertEquals(1, result.size());
        assertEquals(p, result.get(0));
    }

    @Test
    public void testFindByFirstNameAndLastNameAndDob() {
        Person p = new Person("John", "Doe", "dob", "key1");
        when(personRepo.findByFirstNameAndLastNameAndDob("John", "Doe", "dob")).thenReturn(Collections.singletonList(p));

        List<Person> result = service.findByFirstNameAndLastNameAndDob("John", "Doe", "dob");
        assertEquals(1, result.size());
        assertEquals(p, result.get(0));
    }

    @Test
    public void testCreateRecordInvalid() {
        Person p = new Person();
        assertThrows(PersonException.class, () -> service.createRecord(p));
    }

    @Test
    public void testCreateRecordExisting() throws Exception {
        Person p = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        p.setUid("person-1");
        p.setSortKey("JohnDoe2010");

        Person existing = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        existing.setUid("existing-uid");

        when(personRepo.findByFirstNameAndLastNameAndDob("John", "Doe", "01/01/2010"))
                .thenReturn(Collections.singletonList(existing));
        when(personRepo.save(p)).thenReturn(p);

        Person result = service.createRecord(p);
        assertEquals("existing-uid", result.getUid());
    }

    @Test
    public void testCreateRecordNew() throws Exception {
        Person p = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        p.setUid("person-1");
        p.setSortKey("JohnDoe2010");

        when(personRepo.findByFirstNameAndLastNameAndDob("John", "Doe", "01/01/2010"))
                .thenReturn(Collections.emptyList());
        when(personRepo.save(p)).thenReturn(p);

        Person result = service.createRecord(p);
        assertEquals("person-1", result.getUid());
    }

    @Test
    public void testUpdateInvalid() {
        Person p = new Person();
        assertThrows(PersonException.class, () -> service.update(p));
    }

    @Test
    public void testUpdateValid() throws Exception {
        Person p = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        p.setUid("person-1");

        when(personRepo.save(p)).thenReturn(p);

        Person result = service.update(p);
        assertEquals(p, result);
    }

    @Test
    public void testDeleteInvalid() {
        Person p = new Person();
        assertThrows(PersonException.class, () -> service.delete(p));
    }

    @Test
    public void testDeleteNotFound() {
        Person p = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        p.setUid("person-1");

        when(personRepo.findByUid("person-1")).thenReturn(Collections.emptyList());

        assertThrows(PersonNotFoundException.class, () -> service.delete(p));
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        Person p = new Person("John", "Doe", "01/01/2010", "JohnDoe2010");
        p.setUid("person-1");

        when(personRepo.findByUid("person-1")).thenReturn(Collections.singletonList(p));

        service.delete(p);
        verify(personRepo).delete(p);
    }
}
