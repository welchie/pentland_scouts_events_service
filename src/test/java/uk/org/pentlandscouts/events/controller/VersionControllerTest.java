package uk.org.pentlandscouts.events.controller;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@org.springframework.test.context.TestPropertySource(locations = "classpath:application-dev.properties")
public class VersionControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    private static final String VERSION_URL = "/version/get";
    @Test
    public void testGetVersion() {
        ResponseEntity<String> response = restTemplate.getForEntity(VERSION_URL, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
        assertThat(response.getBody(), CoreMatchers.containsString("{\"profiles\":[\"\"],\"errors\":[\"Cannot read version from the Manifest file\"]}"));
    }
}
