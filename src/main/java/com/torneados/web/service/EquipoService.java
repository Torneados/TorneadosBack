package com.torneados.web.service;

import org.springframework.stereotype.Service;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.repositories.EquipoRepository;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final AuthService authService;
    
    public EquipoService(EquipoRepository equipoRepository, 
                         AuthService authService) {
        this.equipoRepository = equipoRepository;
        this.authService = authService;
    }
    
    /**
     * Crea un nuevo equipo.
     * 
     * @param equipo El equipo a crear.
     * @return El equipo creado.
     */
    public Equipo createEquipo(Equipo equipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        equipo.setCreador(currentUser);
        
        return equipoRepository.save(equipo);
    }
    
    /**
     * Obtiene un equipo por su ID.
     * @param idEquipo
     * @return El equipo encontrado.
    */
    public Equipo getEquipoById(Long id) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
    
        // Validar permisos: solo el ADMIN o el creador del equipo pueden ver el equipo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && (equipo.getCreador() == null || !equipo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario()))) {
            throw new AccessDeniedException("Sin permisos para ver este equipo");
        }
        
        return equipo;
    }
    
    /**
     * Actualiza un equipo.
     * 
     * @param idEquipo El ID del equipo a actualizar.
     * @param equipo Los nuevos datos del equipo.
     * @return El equipo actualizado.
     */
    public Equipo updateEquipo(Long idEquipo, Equipo equipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        // Buscar el equipo por ID
        Equipo equipoExistente = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));
        
        //solo el creador o un administrador pueden actualizar el equipo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !equipoExistente.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para actualizar este equipo");
        }
        
        // Actualizar los datos del equipo
        equipoExistente.setNombre(equipo.getNombre());


        return equipoRepository.save(equipoExistente);
    }
    
    
    /**
     * Elimina un equipo.
     * 
     */
    public void deleteEquipo(Long idEquipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));
        
        // Solo el creador o un administrador pueden eliminar el equipo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !equipo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para eliminar este equipo");
        }
        
        equipoRepository.delete(equipo);
    }
}
