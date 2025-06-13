package com.example.backendgroupsmaker.config;

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

import com.example.backendgroupsmaker.security.JwtAuthenticationFilter;
import com.example.backendgroupsmaker.service.JwtService;
import com.example.backendgroupsmaker.service.UtilisateurService;

@Configuration // Indique que cette classe contient une configuration Spring
public class SecurityConfig {

    /**
     * Bean qui permet d’encoder les mots de passe avec l’algorithme BCrypt.
     * C’est une bonne pratique pour stocker les mots de passe de manière sécurisée.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean pour fournir un `AuthenticationManager`, qui est responsable de l’authentification des utilisateurs.
     * Utilisé lors de la connexion pour vérifier les identifiants.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean personnalisé pour notre filtre JWT (authentification par token).
     * On injecte ici notre service de JWT et notre service utilisateur.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            UtilisateurService utilisateurService
    ) {
        return new JwtAuthenticationFilter(jwtService, utilisateurService);
    }

    /**
     * Chaîne de sécurité principale :
     * On y configure toutes les règles de sécurité de l’API.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            // On désactive la protection CSRF (inutile ici car on a une API REST sans session)
            .csrf(csrf -> csrf.disable())

            // On configure les autorisations selon l’URL demandée
            .authorizeHttpRequests(auth -> auth

                // Les routes d’authentification (/api/auth/login, /api/auth/register...) sont publiques
                .requestMatchers("/api/auth/**").permitAll()

                // Les routes d’administration sont réservées aux utilisateurs avec le rôle ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Toutes les autres routes API (comme /api/listes) sont accessibles uniquement si connecté avec le rôle USER
                .requestMatchers("/api/**").hasRole("USER")

                // Toute autre requête (non prévue ci-dessus) nécessite d’être authentifié
                .anyRequest().authenticated()
            )

            // On indique à Spring de ne pas gérer de session HTTP (on est en mode stateless avec JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // On ajoute notre filtre JWT avant le filtre d’authentification standard de Spring
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
