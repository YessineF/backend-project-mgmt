package com.gestion.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitaire JWT — génère et valide les tokens d'authentification.
 * Le token contient : email + rôle de l'utilisateur.
 * Durée de vie : 24 heures.
 */
public class JwtUtil {

    // ⚠️ En production : stocker cette clé dans une variable d'environnement
    private static final String SECRET_KEY =
            "GestionProjetsSecretKey2024XYZ!@#$%ABCDEF1234567890";

    // Durée de validité : 24h en millisecondes
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    private static final Key KEY =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private JwtUtil() {}

    // ── Générer un token JWT ───────────────────────────────────────
    /**
     * @param email  l'email de l'utilisateur (subject du token)
     * @param role   le rôle : "ADMIN" ou "EMPLOYE"
     * @return le token JWT signé
     */
    public static String generateToken(String email, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);   // donnée custom dans le payload

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Extraire l'email du token ──────────────────────────────────
    public static String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // ── Extraire le rôle du token ──────────────────────────────────
    public static String extractRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    // ── Valider un token (signature + expiration) ──────────────────
    /**
     * @return true si le token est valide, false sinon
     */
    public static boolean validateToken(String token) {
        try {
            parseClaims(token);   // lève une exception si invalide
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── Extraire le token depuis le header Authorization ───────────
    /**
     * Le header Angular envoie : "Authorization: Bearer <token>"
     * Cette méthode extrait uniquement le token.
     */
    public static String extractFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // ── Parser privé ──────────────────────────────────────────────
    private static Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}