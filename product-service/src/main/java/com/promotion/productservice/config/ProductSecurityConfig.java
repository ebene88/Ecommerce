package com.promotion.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class ProductSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/product/**").authenticated() // protect product API
                        .anyRequest().permitAll() // allow other endpoints
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()) // enable JWT decoding
                );

        return http.build();
    }
}
