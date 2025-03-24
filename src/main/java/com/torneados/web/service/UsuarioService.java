package com.torneados.web.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;


    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea un nuevo usuario (datos básicos).
     */
    public Usuario createUsuario(Usuario usuario) {
        // validaciones adicionales pendientes
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por Google ID o lo crea si no existe (solo para OAuth2).
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
                newUser.setRol(Usuario.Rol.USUARIO); // Rol por defecto

                return usuarioRepository.save(newUser);
            });
    }

    /**
     * Obtiene el usuario autenticado desde el contexto de seguridad.
     * Soporta tanto OAuth2 (OidcUser) como JWT (User).
     */
    public Usuario getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();

        // OAuth2 con Google
        if (principal instanceof OidcUser oidcUser) {
            String googleId = oidcUser.getSubject();
            return usuarioRepository.findByGoogleId(googleId)
                    .orElseThrow(() -> new AccessDeniedException("Usuario no registrado en la base de datos"));
        }

        // JWT
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String googleId = userDetails.getUsername();
            return usuarioRepository.findByGoogleId(googleId)
                    .orElseThrow(() -> new AccessDeniedException("Usuario no registrado en la base de datos"));
        }

        throw new AccessDeniedException("Usuario no autenticado");
    }

    /**
     * Obtiene los datos de un usuario por su id.
     */
    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    /**
     * Actualiza los datos de un usuario.
     * El usuario actual debe ser el dueño o un administrador.
     */
    @Transactional
    public Usuario updateUsuario(Usuario updatedUsuario) {
        // Obtener el usuario autenticado (JWT u OAuth2)
        Usuario currentUser = getAuthenticatedUser();

        // Validar permisos: solo el dueño o un administrador puede actualizar
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !currentUser.getIdUsuario().equals(updatedUsuario.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para modificar este usuario");
        }

        Usuario user = getUsuarioById(updatedUsuario.getIdUsuario());
        user.setNombre(updatedUsuario.getNombre());
        

        return usuarioRepository.save(user);
    }

    /**
     * Borra un usuario.
     * El usuario actual debe ser el dueño o un administrador.
     */
    public void deleteUsuario(Long id) {
        // Obtener el usuario autenticado
        Usuario currentUser = getAuthenticatedUser();

        // Verificar permisos: solo el dueño o un administrador puede eliminar
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !currentUser.getIdUsuario().equals(id)) {
            throw new AccessDeniedException("Sin permisos para eliminar este usuario");
        }

        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }
    }
}
