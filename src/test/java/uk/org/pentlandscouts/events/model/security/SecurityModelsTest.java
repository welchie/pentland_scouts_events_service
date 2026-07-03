package uk.org.pentlandscouts.events.model.security;

import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SecurityModelsTest {

    @Test
    public void testCookieProperties() {
        CookieProperties props = new CookieProperties();
        props.setDomain("domain");
        props.setPath("path");
        props.setHttpOnly(true);
        props.setSecure(true);
        props.setMaxAgeInMinutes(10);

        assertEquals("domain", props.getDomain());
        assertEquals("path", props.getPath());
        assertTrue(props.isHttpOnly());
        assertTrue(props.isSecure());
        assertEquals(10, props.getMaxAgeInMinutes());
    }

    @Test
    public void testFirebaseProperties() {
        FirebaseProperties props = new FirebaseProperties();
        props.setSessionExpiryInDays(5);
        props.setDatabaseUrl("url");
        props.setEnableStrictServerSession(true);
        props.setEnableCheckSessionRevoked(true);
        props.setEnableLogoutEverywhere(true);

        assertEquals(5, props.getSessionExpiryInDays());
        assertEquals("url", props.getDatabaseUrl());
        assertTrue(props.isEnableStrictServerSession());
        assertTrue(props.isEnableCheckSessionRevoked());
        assertTrue(props.isEnableLogoutEverywhere());
    }

    @Test
    public void testSecurityProperties() {
        SecurityProperties props = new SecurityProperties();
        CookieProperties cookieProps = new CookieProperties();
        FirebaseProperties firebaseProps = new FirebaseProperties();

        props.setCookieProps(cookieProps);
        props.setFirebaseProps(firebaseProps);
        props.setAllowCredentials(true);
        props.setAllowedOrigins(Collections.singletonList("origin"));
        props.setAllowedHeaders(Collections.singletonList("header"));
        props.setExposedHeaders(Collections.singletonList("exposed"));
        props.setAllowedMethods(Collections.singletonList("method"));
        props.setAllowedPublicApis(Collections.singletonList("api"));

        assertEquals(cookieProps, props.getCookieProps());
        assertEquals(firebaseProps, props.getFirebaseProps());
        assertTrue(props.isAllowCredentials());
        assertEquals(Collections.singletonList("origin"), props.getAllowedOrigins());
        assertEquals(Collections.singletonList("header"), props.getAllowedHeaders());
        assertEquals(Collections.singletonList("exposed"), props.getExposedHeaders());
        assertEquals(Collections.singletonList("method"), props.getAllowedMethods());
        assertEquals(Collections.singletonList("api"), props.getAllowedPublicApis());
    }

    @Test
    public void testCredentials() {
        FirebaseToken token = mock(FirebaseToken.class);
        Credentials credentials = new Credentials(Credentials.CredentialType.ID_TOKEN, token, "id", "session");

        assertEquals(Credentials.CredentialType.ID_TOKEN, credentials.getType());
        assertEquals(token, credentials.getDecodedToken());
        assertEquals("id", credentials.getIdToken());
        assertEquals("session", credentials.getSession());

        credentials.setType(Credentials.CredentialType.SESSION);
        assertEquals(Credentials.CredentialType.SESSION, credentials.getType());
    }

    @Test
    public void testUser() {
        User user = new User();
        user.setUid("uid");
        user.setName("name");
        user.setEmail("email");
        user.setEmailVerified(true);
        user.setIssuer("issuer");
        user.setPicture("picture");

        assertEquals("uid", user.getUid());
        assertEquals("name", user.getName());
        assertEquals("email", user.getEmail());
        assertTrue(user.isEmailVerified());
        assertEquals("issuer", user.getIssuer());
        assertEquals("picture", user.getPicture());
    }
}
