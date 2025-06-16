package com.example.backendgroupsmaker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.UtilisateurRepository;

/**
 * Service responsable de la gestion des utilisateurs et de leur authentification.
 */
@Service
public class UtilisateurService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Chargé automatiquement par Spring Security pour l'authentification.
     * Retourne un UserDetails construit à partir du modèle Utilisateur.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));

        // Ne pas préfixer avec "ROLE_"
        return new org.springframework.security.core.userdetails.User(
                utilisateur.getUsername(),
                utilisateur.getPassword(),
                List.of(new SimpleGrantedAuthority(utilisateur.getRole()))
        );
    }

    /**
     * Inscription d'un nouvel utilisateur avec un rôle facultatif.
     * Si aucun rôle n'est fourni, "USER" est appliqué par défaut.
     */
    public Utilisateur inscription(String username, String motDePasse, String role) {
        String encodedPassword = passwordEncoder.encode(motDePasse);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setPassword(encodedPassword);
        utilisateur.setRole((role == null || role.isBlank()) ? "USER" : role.toUpperCase());

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Méthode utilitaire pour récupérer un utilisateur complet (avec rôle) à partir de son username.
     */
    public Utilisateur getUtilisateurByUsername(String username) {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
    }
}
