package ch.dboeckli.springframeworkguru.kbe.inventory.services.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Created by jt on 5/16/20.
 */
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
