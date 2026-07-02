package uk.org.pentlandscouts.events.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.pentlandscouts.events.model.security.CookieProperties;
import uk.org.pentlandscouts.events.model.security.SecurityProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CookieUtilsTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private SecurityProperties restSecProps;

    @Mock
    private CookieProperties cookieProps;

    @InjectMocks
    private CookieUtils cookieUtils;

    private Cookie capturedCookie;

    @BeforeEach
    public void setUp() {
        lenient().when(restSecProps.getCookieProps()).thenReturn(cookieProps);
        lenient().when(cookieProps.getPath()).thenReturn("/");
        lenient().when(cookieProps.getDomain()).thenReturn("localhost");

        capturedCookie = null;
        lenient().doAnswer(invocation -> {
            capturedCookie = invocation.getArgument(0);
            return null;
        }).when(httpServletResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void testGetCookie() {
        Cookie mockCookie = new Cookie("test-cookie", "value");
        mockCookie.setSecure(true);
        mockCookie.setHttpOnly(true);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{mockCookie});

        Cookie result = cookieUtils.getCookie("test-cookie");
        assertNotNull(result);
        assertEquals("value", result.getValue());
    }

    @Test
    public void testSetCookie() {
        cookieUtils.setCookie("test-cookie", "value", 10);

        assertNotNull(capturedCookie);
        assertEquals("test-cookie", capturedCookie.getName());
        assertEquals("value", capturedCookie.getValue());
        assertTrue(capturedCookie.getSecure());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
        assertEquals("localhost", capturedCookie.getDomain());
        assertEquals(10 * 60 * 60, capturedCookie.getMaxAge());
    }

    @Test
    public void testSetSecureCookieWithExpiry() {
        cookieUtils.setSecureCookie("secure-cookie", "sec-value", 30);

        assertNotNull(capturedCookie);
        assertEquals("secure-cookie", capturedCookie.getName());
        assertEquals("sec-value", capturedCookie.getValue());
        assertTrue(capturedCookie.getSecure());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(30 * 60 * 60, capturedCookie.getMaxAge());
    }

    @Test
    public void testSetSecureCookieDefaultExpiry() {
        when(cookieProps.getMaxAgeInMinutes()).thenReturn(60);

        cookieUtils.setSecureCookie("secure-cookie", "sec-value");

        assertNotNull(capturedCookie);
        assertEquals("secure-cookie", capturedCookie.getName());
        assertTrue(capturedCookie.getSecure());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(60 * 60 * 60, capturedCookie.getMaxAge());
    }

    @Test
    public void testDeleteSecureCookie() {
        cookieUtils.deleteSecureCookie("secure-cookie");

        assertNotNull(capturedCookie);
        assertEquals("secure-cookie", capturedCookie.getName());
        assertNull(capturedCookie.getValue());
        assertTrue(capturedCookie.getSecure());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(0, capturedCookie.getMaxAge());
    }

    @Test
    public void testDeleteCookie() {
        cookieUtils.deleteCookie("test-cookie");

        assertNotNull(capturedCookie);
        assertEquals("test-cookie", capturedCookie.getName());
        assertNull(capturedCookie.getValue());
        assertTrue(capturedCookie.getSecure());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(0, capturedCookie.getMaxAge());
    }
}
