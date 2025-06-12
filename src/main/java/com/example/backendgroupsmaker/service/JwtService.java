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

    // Clé secrète utilisée pour signer les tokens JWT.
    // Elle doit être assez longue et sécurisée pour éviter les attaques.
    private static final String SECRET_KEY = "maCleSecreteSuperLonguePourJwtDontLaLongueurEstImportante12345";

    // Durée de validité du token : ici 24 heures (en millisecondes)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Clé générée à partir de la clé secrète, utilisée pour la signature HMAC-SHA256
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Génère un token JWT pour un utilisateur donné.
     * Le token contient le nom d'utilisateur (subject), la date de création et la date d'expiration.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)                 // Stocke le nom d'utilisateur dans le token
                .setIssuedAt(new Date())             // Date de création du token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Date d'expiration
                .signWith(key, SignatureAlgorithm.HS256) // Signature avec la clé secrète et l'algorithme HS256
                .compact();                         // Génère la chaîne de caractères finale du token
    }

    /**
     * Extrait le nom d'utilisateur (subject) depuis un token JWT.
     * Si le token est invalide, une exception sera levée.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)               // Indique la clé pour vérifier la signature
                .build()
                .parseClaimsJws(token)            // Analyse et valide le token JWT
                .getBody()
                .getSubject();                    // Récupère le champ 'subject' (nom d'utilisateur)
    }

    /**
     * Valide un token JWT.
     * Vérifie la signature et la date d'expiration.
     * Retourne true si le token est valide, false sinon.
     */
    public boolean validateToken(String token) {
        try {
            // Tente de parser le token. Si la signature est invalide ou expirée, une exception sera levée.
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true; // Token valide
        } catch (JwtException | IllegalArgumentException e) {
            // Token invalide ou malformé
            return false;
        }
    }
}
