package uk.org.pentlandscouts.events.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import uk.org.pentlandscouts.events.config.AuthProperties;
import uk.org.pentlandscouts.events.model.security.SecurityProperties;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigurationTest {

    @Mock
    private AuthProperties authProperties;

    @Mock
    private SecurityProperties restSecProps;

    @InjectMocks
    private SecurityConfiguration securityConfiguration;

    @Test
    public void testUserDetailsService() {
        when(authProperties.getUserName()).thenReturn("admin");
        when(authProperties.getPassword()).thenReturn("password");
        when(authProperties.getRole()).thenReturn("ADMIN");

        org.springframework.security.crypto.password.PasswordEncoder encoder = securityConfiguration.passwordEncoder();
        assertNotNull(encoder);

        InMemoryUserDetailsManager manager = securityConfiguration.userDetailsService(encoder);
        assertNotNull(manager);
        assertTrue(manager.userExists("admin"));
    }

    @Test
    public void testWebSecurityCustomizer() {
        WebSecurityCustomizer customizer = securityConfiguration.webSecurityCustomizer();
        assertNotNull(customizer);
    }

    @Test
    public void testFilterChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        when(http.cors(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.httpBasic(any())).thenReturn(http);

        DefaultSecurityFilterChain chain = mock(DefaultSecurityFilterChain.class);
        when(http.build()).thenReturn(chain);

        DefaultSecurityFilterChain result = (DefaultSecurityFilterChain) securityConfiguration.filterChain(http);
        assertNotNull(result);
        assertEquals(chain, result);
    }

    @Test
    public void testCorsConfigurationSource() {
        when(restSecProps.getAllowedOrigins()).thenReturn(Collections.singletonList("*"));
        when(restSecProps.getAllowedMethods()).thenReturn(Collections.singletonList("GET"));
        when(restSecProps.getAllowedHeaders()).thenReturn(Collections.singletonList("header"));
        when(restSecProps.isAllowCredentials()).thenReturn(true);
        when(restSecProps.getExposedHeaders()).thenReturn(Collections.singletonList("exposed"));

        org.springframework.web.cors.CorsConfigurationSource source = securityConfiguration.corsConfigurationSource();
        assertNotNull(source);
    }
}
