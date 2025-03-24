package com.torneados.web.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    // Clave secreta en Base64 de 256 bits (32 bytes), EJEMPLO
    private static final String SECRET_KEY_BASE64 = "6v5sE1Jz9HqM3xLr5pQ8WvZdTcYbNkA2F6XsV0DgRfJoLmU=";
    private static final long EXPIRATION_TIME = 3600000; // 1 hora

    // Decodificamos la clave Base64 para usarla con HS256
    private static final Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY_BASE64));

    /**
     * Genera un JWT con el Google ID y el rol del usuario.
     */
    public static String generateToken(String googleId, String role) {
        return Jwts.builder()
                .setSubject(googleId)    // Identificador único del usuario
                .claim("role", role)     // Rol en el token
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
