package com.capstone.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll() // 하늘 수정
            .requestMatchers("/api/health/health_info").permitAll()
            .requestMatchers("/api/health/analysis/**").permitAll()
            .requestMatchers("/ocr/upload").permitAll()
            .requestMatchers("/api/meals/today").permitAll()
            .requestMatchers("/api/meals/weekly/**").permitAll()
            .requestMatchers("/api/meals/recommend").permitAll()
                .anyRequest().authenticated()
            )
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
