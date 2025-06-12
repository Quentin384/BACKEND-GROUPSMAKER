package com.example.backendgroupsmaker.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backendgroupsmaker.service.JwtService;
import com.example.backendgroupsmaker.service.UtilisateurService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// IMPORTANT : plus de @Component car on va gérer ce bean dans SecurityConfig
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtilisateurService utilisateurService;

    // Injection par constructeur, meilleure pratique pour les tests et la clarté
    public JwtAuthenticationFilter(JwtService jwtService, UtilisateurService utilisateurService) {
        this.jwtService = jwtService;
        this.utilisateurService = utilisateurService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        // Vérifie que le header Authorization commence par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extrait le token JWT (sans "Bearer ")
            jwt = authHeader.substring(7);
            try {
                // Extrait le username depuis le token
                username = jwtService.extractUsername(jwt);
            } catch (Exception e) {
                // Ici tu peux logger une erreur si le token est invalide ou malformé
            }
        }

        // Si on a un username et que l'utilisateur n'est pas encore authentifié dans le contexte
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charge les détails de l'utilisateur depuis la base (via UserDetailsService)
            UserDetails userDetails = utilisateurService.loadUserByUsername(username);

            // Vérifie que le token est valide (pas expiré, signature ok...)
            if (jwtService.validateToken(jwt)) {
                // Crée un objet d'authentification Spring avec les droits de l'utilisateur
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // pas de mot de passe car déjà authentifié via token
                                userDetails.getAuthorities());

                // Ajoute les détails de la requête courante à l'authentification
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Place cette authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
