package uk.org.pentlandscouts.events.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import uk.org.pentlandscouts.events.config.AwsProperties;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BarCodeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BarCodeControllerTest {

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

    @Autowired
    private BarCodeController controller;

    @Test
    public void testQrBarcodeURL() throws Exception {
        mockMvc.perform(get("/barcodes/qrcode/")
                .param("url", "https://google.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateImageHttpMessageConverter() {
        HttpMessageConverter<BufferedImage> converter = controller.createImageHttpMessageConverter();
        assertNotNull(converter);
    }
}
