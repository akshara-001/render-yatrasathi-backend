package com.yatrasathi.config;

import com.yatrasathi.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(AbstractHttpConfigurer::disable)

                                .cors(cors -> cors.configurationSource(request -> {
                                        var config = new org.springframework.web.cors.CorsConfiguration();
                                        config.addAllowedOriginPattern("*");
                                        config.addAllowedMethod("*");
                                        config.addAllowedHeader("*");
                                        return config;
                                }))

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // AUTH APIs - PUBLIC
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // GALLERY GET - PUBLIC
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/gallery/**")
                                                .permitAll()

                                                // GALLERY UPLOAD - JWT REQUIRED
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/gallery/upload")
                                                .authenticated()

                                                // GALLERY LIKE/UNLIKE - JWT REQUIRED
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/gallery/*/like")
                                                .authenticated()

                                                // TRAVEL APIs - PUBLIC FOR NOW
                                                .requestMatchers("/api/travel/**").permitAll()

                                                // Everything else - PUBLIC FOR NOW
                                                .anyRequest().permitAll())

                                // Put our JWT filter before Spring's username/password filter
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}