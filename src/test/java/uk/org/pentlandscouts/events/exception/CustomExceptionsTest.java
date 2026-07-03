package uk.org.pentlandscouts.events.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomExceptionsTest {

    @Test
    public void testAwsPropertiesException() {
        AwsPropertiesException ex = new AwsPropertiesException("error");
        assertEquals("error", ex.getMessage());
    }

    @Test
    public void testEventAttendeeException() {
        EventAttendeeException ex = new EventAttendeeException("error");
        assertEquals("error", ex.getMessage());
    }

    @Test
    public void testEventAttendeeNotFoundException() {
        EventAttendeeNotFoundException ex = new EventAttendeeNotFoundException("error");
        assertEquals("error", ex.getMessage());
    }

    @Test
    public void testEventException() {
        EventException ex = new EventException("error");
        assertEquals("error", ex.getMessage());
    }

    @Test
    public void testEventNotFoundException() {
        EventNotFoundException ex = new EventNotFoundException("error");
        assertEquals("error", ex.getMessage());
    }

    @Test
    public void testPersonException() {
        PersonException ex = new PersonException("error");
        assertEquals("error", ex.getMessage());
    }

    @Test
    public void testPersonNotFoundException() {
        PersonNotFoundException ex = new PersonNotFoundException("error");
        assertEquals("error", ex.getMessage());
    }
}
