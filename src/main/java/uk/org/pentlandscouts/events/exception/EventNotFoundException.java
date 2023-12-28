package uk.org.pentlandscouts.events.exception;

public class EventNotFoundException extends Exception {
    public EventNotFoundException(String errorMsg)
    {
        super(errorMsg);
    }
}
