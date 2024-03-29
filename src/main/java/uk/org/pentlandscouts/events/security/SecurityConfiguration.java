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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
                        new AntPathRequestMatcher("/version/get"),
                        new AntPathRequestMatcher("/person/*"),
                        new AntPathRequestMatcher("/person/update/s*"),
                        new AntPathRequestMatcher("/person/find/*"),
                        new AntPathRequestMatcher("/person/all/*"),
                        new AntPathRequestMatcher("/barcodes/qrcode/*"),
                        new AntPathRequestMatcher("/event/*"),
                        new AntPathRequestMatcher("/eventattendee/*"),
                        new AntPathRequestMatcher("/admin/person/import/people/**"),
                        new AntPathRequestMatcher("/swagger-ui/index.html"));
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
