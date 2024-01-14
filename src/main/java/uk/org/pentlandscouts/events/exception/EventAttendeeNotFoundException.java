package uk.org.pentlandscouts.events.exception;

public class EventAttendeeNotFoundException extends Exception {
    public EventAttendeeNotFoundException(String errorMsg)
    {
        super(errorMsg);
    }
}
