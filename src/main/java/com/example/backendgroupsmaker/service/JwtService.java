package com.example.backendgroupsmaker.service;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
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
     * Génère un token JWT pour un utilisateur donné avec son rôle.
     * Le token contient le nom d'utilisateur (subject), le rôle (dans les claims),
     * la date de création et la date d'expiration.
     *
     * @param username Nom d'utilisateur
     * @param role     Rôle de l'utilisateur (ex : "ADMIN", "USER")
     * @return         Le token JWT signé
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)                    // Stocke le nom d'utilisateur dans le token
                .claim("role", role)                     // Stocke le rôle dans les claims
                .setIssuedAt(new Date())                 // Date de création du token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Date d'expiration
                .signWith(key, SignatureAlgorithm.HS256) // Signature avec la clé secrète et l'algo HS256
                .compact();                              // Génère la chaîne de caractères finale du token
    }

    /**
     * Extrait le nom d'utilisateur (subject) depuis un token JWT.
     * Si le token est invalide, une exception sera levée.
     *
     * @param token Token JWT
     * @return Nom d'utilisateur contenu dans le token
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrait le rôle depuis un token JWT.
     * 
     * @param token Token JWT
     * @return Rôle de l'utilisateur (ex : "ADMIN")
     */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Vérifie si un token est valide (signature et expiration).
     *
     * @param token Token JWT à valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token); // Tente de parser les claims (échec = token invalide)
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Méthode interne pour parser les claims du token.
     *
     * @param token Token JWT
     * @return Claims extraits (username, rôle, dates, etc.)
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Vérifie la signature
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
