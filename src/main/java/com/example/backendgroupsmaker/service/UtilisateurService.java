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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur : " + username));

        return new org.springframework.security.core.userdetails.User(
            utilisateur.getUsername(),
            utilisateur.getMotDePasse(),
            List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole()))
        );
    }

    public Utilisateur inscription(String username, String motDePasse) {
        String encodedPassword = passwordEncoder.encode(motDePasse);
        Utilisateur utilisateur = new Utilisateur(username, encodedPassword, "USER");
        return utilisateurRepository.save(utilisateur);
    }
}
