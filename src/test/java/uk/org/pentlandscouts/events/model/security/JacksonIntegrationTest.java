package uk.org.pentlandscouts.events.model.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JacksonIntegrationTest {

    @Test
    public void testLombokAndJacksonSerialization() throws Exception {
        // Test Lombok-generated constructor, getters, setters on User model
        User user = new User();
        user.setUid("test-123");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setEmailVerified(true);
        user.setIssuer("firebase");
        user.setPicture("http://example.com/avatar.jpg");

        // Verify Lombok getters work
        assertEquals("test-123", user.getUid());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertTrue(user.isEmailVerified());
        assertEquals("firebase", user.getIssuer());
        assertEquals("http://example.com/avatar.jpg", user.getPicture());

        // Test Jackson ObjectMapper Serialization/Deserialization
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);

        assertNotNull(json);
        assertTrue(json.contains("\"uid\":\"test-123\""));
        assertTrue(json.contains("\"name\":\"John Doe\""));

        // Deserialization check
        User deserializedUser = objectMapper.readValue(json, User.class);

        assertEquals(user.getUid(), deserializedUser.getUid());
        assertEquals(user.getName(), deserializedUser.getName());
        assertEquals(user.getEmail(), deserializedUser.getEmail());
        assertEquals(user.isEmailVerified(), deserializedUser.isEmailVerified());
        assertEquals(user.getIssuer(), deserializedUser.getIssuer());
        assertEquals(user.getPicture(), deserializedUser.getPicture());
        assertEquals(user, deserializedUser); // Tests Lombok-generated equals
    }
}
