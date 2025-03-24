package com.torneados.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            System.out.println("Token JWT detectado: " + token);

            try {
                token = token.substring(7); // Remover "Bearer "
                Claims claims = JwtUtil.validateToken(token); // Validar token JWT

                String googleId = claims.getSubject(); // Google ID
                String role = claims.get("role", String.class); // Rol del usuario

                System.out.println("Usuario autenticado con ID: " + googleId + " y rol: " + role);

                // Crear objeto User con roles
                User user = new User(googleId, "",
                        Collections.singleton(() -> "ROLE_" + role));

                // Crear autenticación en el contexto de Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                System.out.println("Token expirado: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expirado\"}");
                return;
            } catch (JwtException e) {
                System.out.println("Token inválido: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido\"}");
                return;
            }
        } else {
            System.out.println("No se recibió ningún token JWT en la cabecera Authorization");
        }

        chain.doFilter(request, response);
    }
}
