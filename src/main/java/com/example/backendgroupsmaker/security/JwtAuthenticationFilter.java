package com.example.backendgroupsmaker.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backendgroupsmaker.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtre JWT qui intercepte chaque requête pour valider le token
 * et configurer le contexte de sécurité si le token est valide.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        String role = null;

        // Vérifie que le header Authorization contient un Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(jwt);
                role = jwtService.extractRole(jwt);

                System.out.println("➡️ [JWT Filter] Username extrait : " + username);
                System.out.println("➡️ [JWT Filter] Role extrait : " + role);
            } catch (Exception e) {
                // Token invalide ou erreur de parsing (aucune action, on continue)
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    if (jwtService.validateToken(jwt)) {
        // Création d’une autorité sans préfixe "ROLE_"
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(role));

        // >>> LOG ICI <<<
        System.out.println("[JWT Filter] username=" + username + " | role=" + role);
        System.out.println("[JWT Filter] Authorities = " + authorities);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}


        // Passage au filtre suivant
        filterChain.doFilter(request, response);
    }
}
