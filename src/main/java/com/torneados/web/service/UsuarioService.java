package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.exceptions.AccessDeniedException; // Usamos la excepción personalizada
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.repositories.EquipoRepository;
import com.torneados.web.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;
    private final AuthService authService;
    
    public UsuarioService(UsuarioRepository usuarioRepository, EquipoRepository equipoRepository, AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.authService = authService;
    }

    public Usuario createUsuario(Usuario usuario) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            throw new AccessDeniedException("Sin permisos para crear un usuario");
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new BadRequestException("Ya existe un usuario con ese email.");
        }
    
        return usuarioRepository.save(usuario);
    }
    

    public Usuario getUsuarioById(Long id) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !currentUser.getIdUsuario().equals(id)) {
            throw new AccessDeniedException("Sin permisos para ver este usuario");
        }
        
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
    
    /**
     * Obtiene la lista de equipos de un usuario.
     * 
     * @param idUsuario El ID del usuario.
     * @return La lista de equipos del usuario.
     */
    public List<Equipo> getEquiposByUsuario(Long idUsuario) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) 
                && !currentUser.getIdUsuario().equals(idUsuario)) {
            throw new AccessDeniedException("Sin permisos para acceder a los equipos de otros usuarios");
        }
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));
        
        return equipoRepository.findByCreador(usuario);
    }

    
    @Transactional
    public Usuario updateUsuario(Usuario updatedUsuario) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !currentUser.getIdUsuario().equals(updatedUsuario.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para modificar este usuario");
        }
        
        Usuario user = usuarioRepository.findById(updatedUsuario.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + updatedUsuario.getIdUsuario()));
        
        if (updatedUsuario.getNombre() == null || updatedUsuario.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre es obligatorio.");
        }
        
        user.setNombre(updatedUsuario.getNombre());
        user.setFoto(updatedUsuario.getFoto());
        
        // Solo el ADMINISTRADOR puede actualizar el rol
        if (currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            if (updatedUsuario.getRol() != null) {
                user.setRol(updatedUsuario.getRol());
            }
        } else {
            // Si no es administrador y se intenta cambiar el rol, se lanza excepción
            if (updatedUsuario.getRol() != null && !updatedUsuario.getRol().equals(user.getRol())) {
                throw new AccessDeniedException("No tienes permisos para modificar el rol");
            }
        }
        
        usuarioRepository.save(user);
        return user;
    }



    public void deleteUsuario(Long id) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !currentUser.getIdUsuario().equals(id)) {
            throw new AccessDeniedException("Sin permisos para eliminar este usuario");
        }
        
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        
        usuarioRepository.deleteById(id);
    }

}
