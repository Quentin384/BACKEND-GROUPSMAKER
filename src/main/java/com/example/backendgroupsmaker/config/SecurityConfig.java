package com.example.backendgroupsmaker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.backendgroupsmaker.service.JwtService;
import com.example.backendgroupsmaker.service.UtilisateurService;

@Configuration
public class SecurityConfig {

    // Création du bean PasswordEncoder (chiffrement des mots de passe)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Récupère le AuthenticationManager nécessaire à l'authentification standard Spring
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Création explicite du bean JwtAuthenticationFilter avec injection des dépendances
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UtilisateurService utilisateurService) {
        return new JwtAuthenticationFilter(jwtService, utilisateurService);
    }

    // Configuration de la chaîne de filtres de sécurité HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // Désactivation de la protection CSRF (pas utile dans une API REST stateless)
                .csrf(csrf -> csrf.disable())

                // Définition des règles d’accès
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()       // accessible sans authentification
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // accès réservé aux ADMIN
                        .anyRequest().authenticated())                     // le reste nécessite une authentification

                // Pas de gestion de session (stateless) car utilisation de JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Ajout du filtre JWT avant le filtre standard d’authentification
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
