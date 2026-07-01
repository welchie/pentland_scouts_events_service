package uk.org.pentlandscouts.events.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import uk.org.pentlandscouts.events.config.AuthProperties;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {


    @Autowired
    AuthProperties authProperties;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests((authz) -> authz
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/version/get",
                        "/person/*",
                        "/person/update/s*",
                        "/person/find/*",
                        "/person/all/*",
                        "/barcodes/qrcode/*",
                        "/event/*",
                        "/eventattendee/*",
                        "/admin/person/import/people/**",
                        "/swagger-ui/index.html");
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username(authProperties.getUserName())
                .password(authProperties.getPassword())
                .roles(authProperties.getRole())
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
