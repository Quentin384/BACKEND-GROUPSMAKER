package com.example.backendgroupsmaker.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/signup")
    public String signup(@RequestBody AuthRequest request) {
        Utilisateur utilisateur = utilisateurService.inscription(request.getUsername(), request.getPassword());
        return "Utilisateur créé : " + utilisateur.getUsername();
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        return jwtService.generateToken(request.getUsername());
    }
}
