package com.torneados.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.torneados.web.entities.Usuario;
import com.torneados.web.service.AuthService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para obtener info del usuario autenticado desde el JWT.
     */
    @GetMapping("/user-info")
    public ResponseEntity<Usuario> getUserInfo() {
        try {
            Usuario usuario = authService.getAuthenticatedUser();
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }
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
    /**
     * Endpoint de redirección tras login exitoso con Google OAuth.
     * @throws java.io.IOException 
     */
    @GetMapping("/redirect")
    public void redirectAfterLogin(@AuthenticationPrincipal OidcUser user, HttpServletResponse response) throws IOException, java.io.IOException {
        if (user == null) {
            response.sendRedirect("http://localhost:5173/login-error");
            return;
        }

        Usuario usuario = authService.findOrCreateUser(user);
        String token = authService.generateJwt(user);

        response.sendRedirect("http://localhost:5173/auth/callback?token=" + token + "&id=" + usuario.getIdUsuario());
    }
}
