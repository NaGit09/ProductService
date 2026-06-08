package com.furniro.ProductService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Wishlist APIs require login
                        .requestMatchers(HttpMethod.GET, "/products/wishlist-products").authenticated()
                        .requestMatchers(HttpMethod.POST, "/products/wishlist-products/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/products/wishlist-products/*").authenticated()

                        // Public product APIs
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/category/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/compare").permitAll()

                        // Swagger / Actuator
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**"
                        ).permitAll()

                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}