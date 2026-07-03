package uk.org.pentlandscouts.events.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.org.pentlandscouts.events.model.security.SecurityProperties;
import uk.org.pentlandscouts.events.model.security.FirebaseProperties;
import uk.org.pentlandscouts.events.utils.CookieUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityFilterTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private SecurityProperties securityProps;

    @Mock
    private CookieUtils cookieUtils;

    @Mock
    private FirebaseProperties firebaseProperties;

    @Mock
    private FirebaseAuth firebaseAuth;

    private SecurityFilter securityFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();

        // Subclass SecurityFilter to mock getFirebaseAuth method
        securityFilter = new SecurityFilter() {
            @Override
            protected FirebaseAuth getFirebaseAuth() {
                return firebaseAuth;
            }
        };
        securityFilter.securityService = securityService;
        securityFilter.securityProps = securityProps;
        securityFilter.cookieUtils = cookieUtils;

        when(securityProps.getFirebaseProps()).thenReturn(firebaseProperties);
    }

    @Test
    public void testDoFilterInternalNoTokens() throws Exception {
        when(firebaseProperties.isEnableStrictServerSession()).thenReturn(true);
        when(cookieUtils.getCookie("session")).thenReturn(null);
        when(securityService.getBearerToken(request)).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalWithSessionCookie() throws Exception {
        Cookie cookie = new Cookie("session", "session-val");
        when(cookieUtils.getCookie("session")).thenReturn(cookie);
        when(firebaseProperties.isEnableCheckSessionRevoked()).thenReturn(false);

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("user-1");
        when(firebaseAuth.verifySessionCookie("session-val", false)).thenReturn(decodedToken);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalWithIdToken() throws Exception {
        when(firebaseProperties.isEnableStrictServerSession()).thenReturn(false);
        when(cookieUtils.getCookie("session")).thenReturn(null);
        when(securityService.getBearerToken(request)).thenReturn("id-token-val");

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("user-1");
        when(firebaseAuth.verifyIdToken("id-token-val")).thenReturn(decodedToken);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
