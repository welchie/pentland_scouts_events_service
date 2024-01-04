package uk.org.pentlandscouts.events.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth")
@Data
public class AuthProperties {

    private String userName;
    private String password;
    private String role;
}
