package uk.org.pentlandscouts.events.utils;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.org.pentlandscouts.events.model.security.CookieProperties;
import uk.org.pentlandscouts.events.model.security.SecurityProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CookieUtilsTest {

    private MockHttpServletRequest httpServletRequest;
    private MockHttpServletResponse httpServletResponse;

    @Mock
    private SecurityProperties restSecProps;

    @Mock
    private CookieProperties cookieProps;

    @InjectMocks
    private CookieUtils cookieUtils;

    @BeforeEach
    public void setUp() {
        httpServletRequest = new MockHttpServletRequest();
        httpServletResponse = new MockHttpServletResponse();

        cookieUtils.httpServletRequest = httpServletRequest;
        cookieUtils.httpServletResponse = httpServletResponse;

        lenient().when(restSecProps.getCookieProps()).thenReturn(cookieProps);
        lenient().when(cookieProps.getPath()).thenReturn("/");
        lenient().when(cookieProps.getDomain()).thenReturn("localhost");
    }

    @Test
    public void testGetCookie() {
        Cookie mockCookie = new Cookie("test-cookie", "value");
        mockCookie.setSecure(true);
        mockCookie.setHttpOnly(true);
        httpServletRequest.setCookies(mockCookie);

        Cookie result = cookieUtils.getCookie("test-cookie");
        assertNotNull(result);
        assertEquals("value", result.getValue());
    }

    @Test
    public void testSetCookie() {
        cookieUtils.setCookie("test-cookie", "value", 10);

        Cookie captured = httpServletResponse.getCookie("test-cookie");
        assertNotNull(captured);
        assertEquals("test-cookie", captured.getName());
        assertEquals("value", captured.getValue());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals("/", captured.getPath());
        assertEquals("localhost", captured.getDomain());
        assertEquals(10 * 60 * 60, captured.getMaxAge());
    }

    @Test
    public void testSetSecureCookieWithExpiry() {
        cookieUtils.setSecureCookie("secure-cookie", "sec-value", 30);

        Cookie captured = httpServletResponse.getCookie("secure-cookie");
        assertNotNull(captured);
        assertEquals("secure-cookie", captured.getName());
        assertEquals("sec-value", captured.getValue());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals(30 * 60 * 60, captured.getMaxAge());
    }

    @Test
    public void testSetSecureCookieDefaultExpiry() {
        when(cookieProps.getMaxAgeInMinutes()).thenReturn(60);

        cookieUtils.setSecureCookie("secure-cookie", "sec-value");

        Cookie captured = httpServletResponse.getCookie("secure-cookie");
        assertNotNull(captured);
        assertEquals("secure-cookie", captured.getName());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals(60 * 60 * 60, captured.getMaxAge());
    }

    @Test
    public void testDeleteSecureCookie() {
        cookieUtils.deleteSecureCookie("secure-cookie");

        Cookie captured = httpServletResponse.getCookie("secure-cookie");
        assertNotNull(captured);
        assertEquals("secure-cookie", captured.getName());
        assertNull(captured.getValue());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals(0, captured.getMaxAge());
    }

    @Test
    public void testDeleteCookie() {
        cookieUtils.deleteCookie("test-cookie");

        Cookie captured = httpServletResponse.getCookie("test-cookie");
        assertNotNull(captured);
        assertEquals("test-cookie", captured.getName());
        assertNull(captured.getValue());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals(0, captured.getMaxAge());
    }
}
