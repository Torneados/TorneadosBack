package com.torneados.web.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.torneados.web.entities.Usuario;
import com.torneados.web.repositories.UsuarioRepository;
import com.torneados.web.security.JwtUtil;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene el usuario autenticado desde el contexto de seguridad.
     * Puede ser un OidcUser (OAuth2) o un User (JWT).
     */
    public Usuario getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();

        // 1) Si el principal es OidcUser (autenticación por OAuth2)
        if (principal instanceof OidcUser oidcUser) {
            return findOrCreateUser(oidcUser);
        }

        // 2) Si el principal es un User (autenticación por JWT)
        if (principal instanceof User userDetails) {
            String googleId = userDetails.getUsername();
            return usuarioRepository.findByGoogleId(googleId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));
        }

        throw new RuntimeException("Usuario no autenticado");
    }

    /**
     * Busca un usuario por Google ID o lo crea si no existe (para OAuth2).
     */
    public Usuario findOrCreateUser(OidcUser oidcUser) {
        return usuarioRepository.findByGoogleId(oidcUser.getSubject())
                .orElseGet(() -> {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setGoogleId(oidcUser.getSubject());
                    nuevoUsuario.setNombre(oidcUser.getAttribute("name"));
                    nuevoUsuario.setEmail(oidcUser.getAttribute("email"));
                    nuevoUsuario.setFoto(oidcUser.getAttribute("picture"));
                    nuevoUsuario.setRol(Usuario.Rol.USUARIO); // Rol por defecto
                    return usuarioRepository.save(nuevoUsuario);
                });
    }

    /**
     * Genera un JWT para el usuario autenticado vía OAuth2.
     */
    public String generateJwt(OidcUser oidcUser) {
        Usuario usuario = findOrCreateUser(oidcUser);
        return JwtUtil.generateToken(usuario.getGoogleId(), usuario.getRol().name());
    }
}
