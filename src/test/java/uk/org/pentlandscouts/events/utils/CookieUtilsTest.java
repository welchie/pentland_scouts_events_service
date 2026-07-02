package uk.org.pentlandscouts.events.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @BeforeEach
    public void setUp() {
        lenient().when(restSecProps.getCookieProps()).thenReturn(cookieProps);
        lenient().when(cookieProps.getPath()).thenReturn("/");
        lenient().when(cookieProps.getDomain()).thenReturn("localhost");
    }

    @Test
    public void testGetCookie() {
        Cookie mockCookie = new Cookie("test-cookie", "value");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{mockCookie});

        Cookie result = cookieUtils.getCookie("test-cookie");
        assertNotNull(result);
        assertEquals("value", result.getValue());
    }

    @Test
    public void testSetCookie() {
        cookieUtils.setCookie("test-cookie", "value", 10);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        Cookie captured = cookieCaptor.getValue();
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

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        Cookie captured = cookieCaptor.getValue();
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

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        Cookie captured = cookieCaptor.getValue();
        assertEquals("secure-cookie", captured.getName());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals(60 * 60 * 60, captured.getMaxAge());
    }

    @Test
    public void testDeleteSecureCookie() {
        cookieUtils.deleteSecureCookie("secure-cookie");

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        Cookie captured = cookieCaptor.getValue();
        assertEquals("secure-cookie", captured.getName());
        assertNull(captured.getValue());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals(0, captured.getMaxAge());
    }

    @Test
    public void testDeleteCookie() {
        cookieUtils.deleteCookie("test-cookie");

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        Cookie captured = cookieCaptor.getValue();
        assertEquals("test-cookie", captured.getName());
        assertNull(captured.getValue());
        assertTrue(captured.getSecure());
        assertTrue(captured.isHttpOnly());
        assertEquals(0, captured.getMaxAge());
    }
}
