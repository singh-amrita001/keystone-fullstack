package com.zidio.keystone.config;

import com.zidio.keystone.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =====================================================
    // AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
    
    
    // =====================================================
    // 401 UNAUTHORIZED HANDLER
    // =====================================================
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Authentication required"
                    }
                    """);
        };
    }

    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================
                .csrf(csrf -> csrf.disable())

                // =================================================
                // CORS
                // =================================================
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource
                ))

                // =================================================
                // SESSION MANAGEMENT
                // JWT = STATELESS
                // =================================================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================
                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // PUBLIC APIs
                        // =================================================
                        .requestMatchers(
                                "/api/auth/**",
                                "/uploads/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // =================================================
                        // GENERAL DASHBOARD
                        // ADMIN + DISPATCHER + TECHNICIAN
                        // =================================================
                        .requestMatchers(
                                "/api/dashboard",
                                "/api/dashboard/activity"
                        ).hasAnyRole(
                                "ADMIN",
                                "DISPATCHER",
                                "TECHNICIAN"
                        )

                        // =================================================
                        // DISPATCHER DASHBOARD
                        // ADMIN + DISPATCHER
                        // =================================================
                        .requestMatchers(
                                "/api/dashboard/dispatcher"
                        ).hasAnyRole(
                                "ADMIN",
                                "DISPATCHER"
                        )

                        // =================================================
                        // WORK ORDER HISTORY
                        // =================================================
                        .requestMatchers(
                                "/api/work-orders/*/history"
                        ).hasAnyRole(
                                "ADMIN",
                                "DISPATCHER",
                                "TECHNICIAN",
                                "CUSTOMER"
                        )

                        // =================================================
                        // WORK ORDERS
                        // =================================================
                        .requestMatchers(
                                "/api/work-orders/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "DISPATCHER",
                                "TECHNICIAN",
                                "CUSTOMER"
                        )

                        // =================================================
                        // TECHNICIAN LIST
                        // =================================================
                        .requestMatchers(
                                "/api/users/technicians"
                        )
                        .hasAuthority("ROLE_ADMIN")
                        // =================================================
                        // ALL USERS
                        // ADMIN ONLY
                        // =================================================
                        .requestMatchers(
                                "/api/users"
                        ).hasRole("ADMIN")
                        .requestMatchers("/api/users/*/role")
                        .hasRole("ADMIN")

                        // =================================================
                        // PROFILE
                        // =================================================
                        .requestMatchers(
                                "/api/users/profile/**",
                                "/api/users/change-password/**",
                                "/api/users/notification-preferences/**"
                        ).authenticated()

                        // =================================================
                        // OTHER APIs
                        // =================================================
                        .anyRequest().authenticated()
                )
                
             // =================================================
                // 401 UNAUTHORIZED
                // =================================================

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                authenticationEntryPoint()
                        )
                )

                // =================================================
                // JWT AUTHENTICATION FILTER
                // =================================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}