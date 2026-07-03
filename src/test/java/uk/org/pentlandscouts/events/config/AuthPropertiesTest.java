package uk.org.pentlandscouts.events.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthPropertiesTest {

    @Test
    public void testGettersAndSetters() {
        AuthProperties props = new AuthProperties();
        props.setUserName("user");
        props.setPassword("pass");
        props.setRole("role");

        assertEquals("user", props.getUserName());
        assertEquals("pass", props.getPassword());
        assertEquals("role", props.getRole());
    }
}
