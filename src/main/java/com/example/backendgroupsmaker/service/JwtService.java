package com.example.backendgroupsmaker.service;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Clé secrète pour signer les tokens (doit être suffisamment longue)
    private static final String SECRET_KEY = "maCleSecreteSuperLonguePourJwtDontLaLongueurEstImportante12345";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 heures

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Génère un token pour un username donné
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Récupère le username depuis un token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Valide un token (expiration, signature...)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token invalide (expiration, signature incorrecte, etc.)
            return false;
        }
    }
}
