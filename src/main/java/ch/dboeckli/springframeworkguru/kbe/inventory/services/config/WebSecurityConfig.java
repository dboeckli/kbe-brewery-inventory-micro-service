package ch.dboeckli.springframeworkguru.kbe.inventory.services.config;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()  // permit all actuator endpoints
                .anyRequest().authenticated()
            )
            
            .httpBasic(httpBasic -> {});

        return http.build();
    }
}
