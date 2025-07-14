package com.techlab.ecommerce.config;

import com.techlab.ecommerce.util.JwtUtil;
import com.techlab.ecommerce.service.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, AppUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // ===== Permitir OPTIONS (CORS preflight) =====
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ===== RUTAS PUBLICAS =====
                        .requestMatchers("/api/v0.1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v0.1/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v0.1/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v0.1/categories/**").permitAll()

                        // ===== ROLES ESPECÍFICOS =====
                        // Usuario autenticado
                        .requestMatchers("/api/v0.1/cart/**").hasRole("USER")
                        .requestMatchers("/api/v0.1/orders").hasRole("USER")
                        .requestMatchers("/api/v0.1/users/me").hasRole("USER")

                        // Admin - productos y categorías
                        .requestMatchers(HttpMethod.POST, "/api/v0.1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v0.1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v0.1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v0.1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v0.1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v0.1/categories/**").hasRole("ADMIN")

                        // Admin - usuarios (excepto /me que ya está arriba)
                        .requestMatchers("/api/v0.1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v0.1/orders/all").hasRole("ADMIN")

                        // ===== CATCH-ALL (al final) =====
                        .anyRequest().authenticated())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}