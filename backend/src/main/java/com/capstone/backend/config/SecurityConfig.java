package com.capstone.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;
import jakarta.servlet.MultipartConfigElement;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // increase limits as required (example: 20MB per file, 50MB per request)
        factory.setMaxFileSize(DataSize.ofMegabytes(20));
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        return factory.createMultipartConfig();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            // auth-related open endpoints
            .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/check-id").permitAll()
            // permit address-related endpoints (list/add/current/changeCurrent) without auth
            .requestMatchers("/api/auth/users/*/addresses/**").permitAll() // <-- broadened to include /current and other address subpaths
            // allow CORS preflight
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // other user endpoints require auth
            .requestMatchers("/api/auth/users/**").authenticated()
            .requestMatchers("/api/health/health_info").authenticated()
            .requestMatchers("/api/health/analysis/**").permitAll()
            .requestMatchers("/api/health/upload").permitAll()
            .requestMatchers("/api/health/upload-complete").permitAll()
            .requestMatchers("/ocr/upload").permitAll()
            .requestMatchers("/api/meals/today").permitAll()
            .requestMatchers("/api/meals/weekly/**").permitAll()
            .requestMatchers("/api/meals/recommend").permitAll()
            .requestMatchers("/api/foods/**").permitAll()
            .requestMatchers("/api/delivery/**").permitAll()
                .anyRequest().authenticated()
            )
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
