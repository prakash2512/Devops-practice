package com.healthcare.configuation;
import com.healthcare.filter.JwtAuthFilter;
import com.healthcare.service.implement.AuthServiceImplements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration()
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthServiceImplements authServiceImplements;
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthEntryPoint authEntryPoint;

    @Autowired
    public SecurityConfig(AuthServiceImplements authServiceImplements, JwtAuthFilter jwtAuthFilter, CustomAuthEntryPoint authEntryPoint) {
        this.authServiceImplements = authServiceImplements;
        this.jwtAuthFilter = jwtAuthFilter;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Configure endpoint access
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints: login and user creation
                        .requestMatchers("/auth/v1/login", "/auth/v1/create/user", "/auth/v1/newPassword","/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // All other endpoints require JWT
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                )
                // Stateless session management
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add JWT filter before Spring Security authentication
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedOrigins(Arrays.asList(
                "http://34.235.7.203:3000",
                "http://localhost:3000",
                "http://localhost:3001",
                "http://34.235.7.203:3001",
                "http://52.206.194.254:80",
                "http://52.206.194.254",
                "http://52.206.194.254:443",
                "https://medelitereports.com",
                "https://www.medelitereports.com",
                "http://www.medelitereports.com",
                "http://medelitereports.com",
                "http://127.0.0.1:3000"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailService() {
        return authServiceImplements;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }
}


