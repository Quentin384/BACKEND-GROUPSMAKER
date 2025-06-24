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
 * Filtre JWT : extrait et valide le token, nettoie le préfixe 'ROLE_' pour
 * ne conserver que 'USER' ou 'ADMIN', puis configure le contexte de sécurité.
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

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String username = null;
            String rawRole = null;

            try {
                username = jwtService.extractUsername(jwt);
                rawRole = jwtService.extractRole(jwt);
            } catch (Exception e) {
                // Token invalide ou mal formé, on continue sans authentification
            }

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtService.validateToken(jwt)) {

                // Nettoyage du préfixe 'ROLE_' pour conserver 'USER' ou 'ADMIN'
                String role = (rawRole != null && rawRole.startsWith("ROLE_"))
                        ? rawRole.substring(5)
                        : rawRole;

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(authority)
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}