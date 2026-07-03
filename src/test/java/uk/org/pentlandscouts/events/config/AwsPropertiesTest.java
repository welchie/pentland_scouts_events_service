package uk.org.pentlandscouts.events.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AwsPropertiesTest {

    @Test
    public void testGettersAndSetters() {
        AwsProperties props = new AwsProperties();
        props.setAccessKey("access");
        props.setSecretKey("secret");
        props.setRegion("region");
        props.setEndPointURL("url");
        props.setTablePrefix("prefix");

        assertEquals("access", props.getAccessKey());
        assertEquals("secret", props.getSecretKey());
        assertEquals("region", props.getRegion());
        assertEquals("url", props.getEndPointURL());
        assertEquals("prefix", props.getTablePrefix());
    }
}
