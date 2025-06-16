package com.example.backendgroupsmaker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.service.JwtService;
import com.example.backendgroupsmaker.service.UtilisateurService;

/**
 * Contrôleur pour gérer l'inscription et la connexion via JWT.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    /**
     * Inscription d’un nouvel utilisateur avec un rôle personnalisé.
     * Si le rôle est absent ou vide, "USER" est utilisé par défaut.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest request) {
        Utilisateur utilisateur = utilisateurService.inscription(
            request.getUsername(),
            request.getPassword(),
            request.getRole()
        );
        return ResponseEntity.ok("Utilisateur créé : " + utilisateur.getUsername());
    }

    /**
     * Authentifie l'utilisateur et retourne un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Authentifie les identifiants
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            // Récupère l'utilisateur pour extraire son rôle
            Utilisateur utilisateur = utilisateurService
                .getUtilisateurByUsername(request.getUsername());

            // Génère un token avec username + rôle
            String token = jwtService.generateToken(
                utilisateur.getUsername(),
                utilisateur.getRole()
            );

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Échec de connexion : " + e.getMessage());
        }
    }
}
