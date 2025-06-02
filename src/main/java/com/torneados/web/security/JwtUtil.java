package com.torneados.web.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    private static final long EXPIRATION_TIME = 3600000; // 1 hora

    // Creamos la Key a partir de la variable de entorno:
    private static final Key key;
    static {
        String secretBase64 = System.getenv("JWT_SECRET_KEY_BASE64");
        if (secretBase64 == null || secretBase64.isEmpty()) {
            throw new IllegalStateException("Falta la variable de entorno JWT_SECRET_KEY_BASE64");
        }
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretBase64));
    }

    /**
     * Genera un JWT con el Google ID y el rol del usuario.
     */
    public static String generateToken(String googleId, String role) {
        return Jwts.builder()
                   .setSubject(googleId)
                   .claim("role", role)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    /**
     * Valida el token y devuelve los claims.
     */
    public static Claims validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
}
