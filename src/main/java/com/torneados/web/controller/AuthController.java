package com.torneados.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.torneados.web.entities.Usuario;
import com.torneados.web.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para obtener info del usuario autenticado.
     */
    @GetMapping("/user-info")
    public ResponseEntity<Usuario> getUserInfo(@AuthenticationPrincipal OidcUser user) {
        if (user == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // Obtener o crear el usuario en la base de datos a través de AuthService
        return ResponseEntity.ok(authService.findOrCreateUser(user));
    }

    /**
     * Endpoint para generar JWT cuando el usuario inicie sesión.
     */
    @GetMapping("/token")
    public ResponseEntity<String> generateJwt(@AuthenticationPrincipal OidcUser user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Error: Usuario no autenticado");
        }

        // Delegamos la generación del JWT al AuthService
        String jwt = authService.generateJwt(user);

        return ResponseEntity.ok(jwt);
    }
}
