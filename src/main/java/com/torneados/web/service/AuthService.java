package com.torneados.web.service;

import org.springframework.security.access.AccessDeniedException;
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
     * Busca un usuario por Google ID o lo crea si no existe (OAuth2).
     */
    public Usuario findOrCreateUser(OidcUser oidcUser) {
        String googleId = oidcUser.getSubject();
        String email = oidcUser.getAttribute("email");
        String nombre = oidcUser.getAttribute("name");
        String foto = oidcUser.getAttribute("picture");

        return usuarioRepository.findByGoogleId(googleId)
            .orElseGet(() -> {
                Usuario newUser = new Usuario();
                newUser.setGoogleId(googleId);
                newUser.setEmail(email);
                newUser.setNombre(nombre);
                newUser.setFoto(foto);
                newUser.setRol(Usuario.Rol.USUARIO);
                return usuarioRepository.save(newUser);
            });
    }

    /**
     * Obtiene el usuario autenticado desde el contexto de seguridad.
     */
    public Usuario getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            String googleId = oidcUser.getSubject();
            return usuarioRepository.findByGoogleId(googleId)
                    .orElseThrow(() -> new AccessDeniedException("Usuario no registrado en la base de datos"));
        }

        if (principal instanceof User userDetails) {
            return usuarioRepository.findByGoogleId(userDetails.getUsername())
                    .orElseThrow(() -> new AccessDeniedException("Usuario no registrado en la base de datos"));
        }

        throw new AccessDeniedException("Usuario no autenticado");
    }

    /**
     * Genera un JWT para el usuario autenticado vía OAuth2.
     */
    public String generateJwt(OidcUser oidcUser) {
        Usuario usuario = findOrCreateUser(oidcUser);
        return JwtUtil.generateToken(usuario.getGoogleId(), usuario.getRol().name());
    }
}
