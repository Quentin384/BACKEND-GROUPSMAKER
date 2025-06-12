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

@Service
public class UtilisateurService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Charge un utilisateur à partir de son nom d'utilisateur.
     * Cette méthode est utilisée par Spring Security lors de l'authentification.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche l'utilisateur dans la base de données via le repository
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur : " + username));

        // Retourne un objet UserDetails attendu par Spring Security
        return new org.springframework.security.core.userdetails.User(
            utilisateur.getUsername(),          // nom d'utilisateur
            utilisateur.getPassword(),          // mot de passe encodé (bcrypt)
            List.of(new SimpleGrantedAuthority("ROLE_USER")) // liste des rôles (ici ROLE_USER fixe)
        );
    }

    /**
     * Inscrit un nouvel utilisateur avec un nom d'utilisateur et un mot de passe.
     * Le mot de passe est encodé avec BCrypt avant sauvegarde.
     */
    public Utilisateur inscription(String username, String motDePasse) {
        // Encodage du mot de passe pour ne jamais stocker le mot de passe en clair
        String encodedPassword = passwordEncoder.encode(motDePasse);

        // Création d’un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setPassword(encodedPassword);

        // Enregistrement en base de données via le repository
        return utilisateurRepository.save(utilisateur);
    }
}
