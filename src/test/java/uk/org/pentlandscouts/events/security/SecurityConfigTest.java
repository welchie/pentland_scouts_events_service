package uk.org.pentlandscouts.events.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfigurationSource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.org.pentlandscouts.events.model.security.SecurityProperties;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SecurityProperties restSecProps;

    @Mock
    private SecurityFilter tokenAuthenticationFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    public void testRestAuthenticationEntryPoint() throws Exception {
        AuthenticationEntryPoint entryPoint = securityConfig.restAuthenticationEntryPoint();
        assertNotNull(entryPoint);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        entryPoint.commence(request, response, null);

        verify(response).setStatus(401);
        verify(response).setContentType("application/json;charset=UTF-8");
        assertEquals("{}", stringWriter.toString().trim());
    }

    @Test
    public void testCorsConfigurationSource() {
        when(restSecProps.getAllowedOrigins()).thenReturn(Collections.singletonList("*"));
        when(restSecProps.getAllowedMethods()).thenReturn(Collections.singletonList("GET"));
        when(restSecProps.getAllowedHeaders()).thenReturn(Collections.singletonList("header"));
        when(restSecProps.isAllowCredentials()).thenReturn(true);
        when(restSecProps.getExposedHeaders()).thenReturn(Collections.singletonList("exposed"));

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);
    }

    @Test
    public void testConfigure() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        when(http.httpBasic(any())).thenReturn(http);
        lenient().when(restSecProps.getAllowedPublicApis()).thenReturn(Collections.singletonList("/api"));

        securityConfig.configure(http);

        verify(http).addFilterBefore(tokenAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    }
}
