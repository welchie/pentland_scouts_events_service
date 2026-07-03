package uk.org.pentlandscouts.events.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import uk.org.pentlandscouts.events.config.AwsProperties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AwsProperties awsProperties;

    @MockBean
    private DynamoDbClient dynamoDbClient;

    @MockBean
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @MockBean
    private DynamoDbTemplate dynamoDbTemplate;

    @MockBean
    private DynamoDbTableNameResolver dynamoDbTableNameResolver;

    @Test
    public void testCreateEventTableAwsPropertiesError() throws Exception {
        when(awsProperties.getRegion()).thenReturn("");

        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/admin/event/create/eventtable"));
        });
    }

    @Test
    public void testCreateEventTableConnectionFailure() {
        when(awsProperties.getRegion()).thenReturn("us-east-1");
        when(awsProperties.getAccessKey()).thenReturn("dummy-key");
        when(awsProperties.getSecretKey()).thenReturn("dummy-secret");
        when(awsProperties.getEndPointURL()).thenReturn("http://localhost:18000"); // dummy port that refuses connection

        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/admin/event/create/eventtable"));
        });
    }

    @Test
    public void testCreateEventAttendeeHistTableConnectionFailure() {
        when(awsProperties.getRegion()).thenReturn("us-east-1");
        when(awsProperties.getAccessKey()).thenReturn("dummy-key");
        when(awsProperties.getSecretKey()).thenReturn("dummy-secret");
        when(awsProperties.getEndPointURL()).thenReturn("http://localhost:18000");

        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/admin/event/create/eventattendeehisttable"));
        });
    }

    @Test
    public void testCreateEventAttendeeTableConnectionFailure() {
        when(awsProperties.getRegion()).thenReturn("us-east-1");
        when(awsProperties.getAccessKey()).thenReturn("dummy-key");
        when(awsProperties.getSecretKey()).thenReturn("dummy-secret");
        when(awsProperties.getEndPointURL()).thenReturn("http://localhost:18000");

        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/admin/event/create/eventattendeetable"));
        });
    }

    @Test
    public void testCreateEventTableSuccess() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            String target = exchange.getRequestHeaders().getFirst("X-Amz-Target");
            String response = "{}";
            if (target != null) {
                if (target.contains("CreateTable")) {
                    response = "{\"TableDescription\":{\"TableName\":\"Event\",\"TableStatus\":\"CREATING\"}}";
                } else if (target.contains("DescribeTable")) {
                    response = "{\"Table\":{\"TableName\":\"Event\",\"TableStatus\":\"ACTIVE\"}}";
                }
            }
            byte[] bytes = response.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/x-amz-json-1.0");
            exchange.sendResponseHeaders(200, bytes.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        int port = server.getAddress().getPort();

        try {
            when(awsProperties.getRegion()).thenReturn("us-east-1");
            when(awsProperties.getAccessKey()).thenReturn("dummy-key");
            when(awsProperties.getSecretKey()).thenReturn("dummy-secret");
            when(awsProperties.getEndPointURL()).thenReturn("http://localhost:" + port);

            mockMvc.perform(get("/admin/event/create/eventtable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0]").value("Event table was created"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void testCreateEventAttendeeHistTableSuccess() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            String target = exchange.getRequestHeaders().getFirst("X-Amz-Target");
            String response = "{}";
            if (target != null) {
                if (target.contains("CreateTable")) {
                    response = "{\"TableDescription\":{\"TableName\":\"EventAttendeeHist\",\"TableStatus\":\"CREATING\"}}";
                } else if (target.contains("DescribeTable")) {
                    response = "{\"Table\":{\"TableName\":\"EventAttendeeHist\",\"TableStatus\":\"ACTIVE\"}}";
                }
            }
            byte[] bytes = response.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/x-amz-json-1.0");
            exchange.sendResponseHeaders(200, bytes.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        int port = server.getAddress().getPort();

        try {
            when(awsProperties.getRegion()).thenReturn("us-east-1");
            when(awsProperties.getAccessKey()).thenReturn("dummy-key");
            when(awsProperties.getSecretKey()).thenReturn("dummy-secret");
            when(awsProperties.getEndPointURL()).thenReturn("http://localhost:" + port);

            mockMvc.perform(get("/admin/event/create/eventattendeehisttable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0]").value("EventAttendeeHist table was created"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void testCreateEventAttendeeTableSuccess() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            String target = exchange.getRequestHeaders().getFirst("X-Amz-Target");
            String response = "{}";
            if (target != null) {
                if (target.contains("CreateTable")) {
                    response = "{\"TableDescription\":{\"TableName\":\"EventAttendee\",\"TableStatus\":\"CREATING\"}}";
                } else if (target.contains("DescribeTable")) {
                    response = "{\"Table\":{\"TableName\":\"EventAttendee\",\"TableStatus\":\"ACTIVE\"}}";
                }
            }
            byte[] bytes = response.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/x-amz-json-1.0");
            exchange.sendResponseHeaders(200, bytes.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        int port = server.getAddress().getPort();

        try {
            when(awsProperties.getRegion()).thenReturn("us-east-1");
            when(awsProperties.getAccessKey()).thenReturn("dummy-key");
            when(awsProperties.getSecretKey()).thenReturn("dummy-secret");
            when(awsProperties.getEndPointURL()).thenReturn("http://localhost:" + port);

            mockMvc.perform(get("/admin/event/create/eventattendeetable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0]").value("EventAttendee table was created"));
        } finally {
            server.stop(0);
        }
    }
}
