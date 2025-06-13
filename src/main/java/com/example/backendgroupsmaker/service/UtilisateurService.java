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
 * Service chargé de gérer les utilisateurs et leur authentification.
 * Implémente UserDetailsService, ce qui permet à Spring Security
 * de charger un utilisateur depuis la base de données.
 */
@Service
public class UtilisateurService implements UserDetailsService {

    // Injecte automatiquement le repository pour accéder à la base de données
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Injecte automatiquement l'encodeur de mot de passe (BCrypt)
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cette méthode est appelée automatiquement par Spring Security
     * lorsqu'un utilisateur tente de se connecter.
     *
     * Elle cherche un utilisateur par son nom, et construit un objet
     * UserDetails (utilisé pour la vérification des identifiants).
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cherche un utilisateur par son nom dans la base de données
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));

        // Crée un objet que Spring Security peut utiliser pour l’authentification
        return new org.springframework.security.core.userdetails.User(
            utilisateur.getUsername(),                    // nom d'utilisateur
            utilisateur.getPassword(),                    // mot de passe encodé
            List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole())) // rôle avec préfixe ROLE_
        );
    }

    /**
     * Inscrit un nouvel utilisateur dans la base de données.
     * Le mot de passe est encodé pour des raisons de sécurité,
     * et le rôle est défini par défaut à "USER".
     *
     * @param username Le nom d'utilisateur
     * @param motDePasse Le mot de passe en clair (fourni par le client)
     * @return L'objet Utilisateur sauvegardé
     */
    public Utilisateur inscription(String username, String motDePasse) {
        // Encodage du mot de passe avant sauvegarde (ex : BCrypt)
        String encodedPassword = passwordEncoder.encode(motDePasse);

        // Création d’un nouvel objet utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setPassword(encodedPassword);
        utilisateur.setRole("USER"); // Définir un rôle par défaut

        // Sauvegarde dans la base de données via le repository
        return utilisateurRepository.save(utilisateur);
    }
}
