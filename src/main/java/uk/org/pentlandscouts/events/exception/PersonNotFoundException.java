package uk.org.pentlandscouts.events.exception;

public class PersonNotFoundException extends Exception {
    public PersonNotFoundException(String errorMsg)
    {
        super(errorMsg);
    }
}
