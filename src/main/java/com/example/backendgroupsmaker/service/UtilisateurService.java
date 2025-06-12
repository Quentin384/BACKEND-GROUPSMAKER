package com.example.backendgroupsmaker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur inscription(String username, String motDePasse) {
        if (utilisateurRepository.existsByUsername(username)) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé.");
        }

        String motDePasseHash = passwordEncoder.encode(motDePasse);
        Utilisateur utilisateur = new Utilisateur(username, motDePasseHash, "USER");
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur chercherParNom(String username) {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
