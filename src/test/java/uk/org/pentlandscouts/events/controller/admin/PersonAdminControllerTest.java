package uk.org.pentlandscouts.events.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import uk.org.pentlandscouts.events.config.AwsProperties;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.service.EventAttendeeService;
import uk.org.pentlandscouts.events.service.PersonService;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PersonAdminControllerTest {

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

    @MockBean
    private PersonService personService;

    @MockBean
    private EventAttendeeService eventAttendeeService;

    @Test
    public void testImportPeopleEmptyFile() throws Exception {
        var result = mockMvc.perform(post("/admin/person/import/people")
                .param("eventUid", "event-1"))
                .andReturn();
        System.out.println("DEBUG_STATUS: " + result.getResponse().getStatus());
        System.out.println("DEBUG_BODY: " + result.getResponse().getContentAsString());
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testImportPeopleSuccess() throws Exception {
        Person mockPerson = new Person("John", "Doe", "01/01/2010", "JohnDoe01/01/2010");
        mockPerson.setUid("person-1");
        mockPerson.setPhotoPermission("true");
        when(personService.createRecord(any(Person.class))).thenReturn(mockPerson);

        EventAttendee mockAttendee = new EventAttendee("event-1", "person-1", "true");
        when(eventAttendeeService.createRecord(any(EventAttendee.class))).thenReturn(mockAttendee);

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            for (int col = 0; col < 13; col++) {
                header.createCell(col).setCellValue("Header " + col);
            }

            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("John");
            dataRow.createCell(1).setCellValue("Doe");
            dataRow.createCell(2).setCellValue("Group:Scouts");
            dataRow.createCell(3).setCellValue("CampA");
            dataRow.createCell(4).setCellValue("01/01/2010");
            dataRow.createCell(5).setCellValue("john.doe@test.com");
            dataRow.createCell(6).setCellValue("true");
            dataRow.createCell(7).setCellValue("None");
            dataRow.createCell(8).setCellValue("None");
            dataRow.createCell(9).setCellValue("None");
            dataRow.createCell(10).setCellValue("Jane Doe");
            dataRow.createCell(11).setCellValue("07777777777");
            dataRow.createCell(12).setCellValue("Mother");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());

            mockMvc.perform(multipart("/admin/person/import/people")
                    .file(file)
                    .param("eventUid", "event-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.EventAttendees").isArray());
        }
    }

    @Test
    public void testCreatePersonTableSuccess() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            String target = exchange.getRequestHeaders().getFirst("X-Amz-Target");
            String response = "{}";
            if (target != null) {
                if (target.contains("CreateTable")) {
                    response = "{\"TableDescription\":{\"TableName\":\"Person\",\"TableStatus\":\"CREATING\"}}";
                } else if (target.contains("DescribeTable")) {
                    response = "{\"Table\":{\"TableName\":\"Person\",\"TableStatus\":\"ACTIVE\"}}";
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

            mockMvc.perform(get("/admin/person/create/persontable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0]").value("Person table was created"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void testExportToExcelSuccess() throws Exception {
        java.util.List<Person> list = java.util.Collections.singletonList(new Person("John", "Doe", "dob", "key"));
        when(personService.findAll()).thenReturn(list);

        mockMvc.perform(get("/admin/person/export/people/excel/all"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment; filename=people_")))
                .andExpect(content().contentType("application/octet-stream"));
    }
}
