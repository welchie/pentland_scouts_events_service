package uk.org.pentlandscouts.events.utils;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class EventUtilsTest {

    @Test
    public void testGenerateType1UUID() {
        UUID uuid = EventUtils.generateType1UUID();
        assertNotNull(uuid);
        assertEquals(1, uuid.version());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(EventUtils.isEmpty(null));
        assertTrue(EventUtils.isEmpty(""));
        assertFalse(EventUtils.isEmpty("not-empty"));
    }
}
