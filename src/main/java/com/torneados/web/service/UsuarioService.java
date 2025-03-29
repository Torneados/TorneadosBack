package com.torneados.web.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;

    public UsuarioService(UsuarioRepository usuarioRepository, AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
    }

    public Usuario createUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public Usuario updateUsuario(Usuario updatedUsuario) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !currentUser.getIdUsuario().equals(updatedUsuario.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para modificar este usuario");
        }

        Usuario user = getUsuarioById(updatedUsuario.getIdUsuario());
        user.setNombre(updatedUsuario.getNombre());

        return usuarioRepository.save(user);
    }

    public void deleteUsuario(Long id) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !currentUser.getIdUsuario().equals(id)) {
            throw new AccessDeniedException("Sin permisos para eliminar este usuario");
        }

        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }
    }
}
