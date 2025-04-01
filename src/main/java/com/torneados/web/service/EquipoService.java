package com.torneados.web.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.EquipoJugadores;
import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.Usuario;
import com.torneados.web.entities.ids.EquipoJugadoresId;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.repositories.EquipoRepository;
import com.torneados.web.repositories.EquipoJugadoresRepository;
import com.torneados.web.repositories.JugadorRepository;
import com.torneados.web.repositories.UsuarioRepository;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final JugadorRepository jugadorRepository;
    private final EquipoJugadoresRepository equipoJugadoresRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    
    public EquipoService(EquipoRepository equipoRepository, 
                         JugadorRepository jugadorRepository, 
                         EquipoJugadoresRepository equipoJugadoresRepository,
                         UsuarioRepository usuarioRepository,
                         AuthService authService) {
        this.equipoRepository = equipoRepository;
        this.jugadorRepository = jugadorRepository;
        this.equipoJugadoresRepository = equipoJugadoresRepository;
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
    }
    
    /**
     * Crea un equipo.
     * 
     */
    public Equipo createEquipo(Equipo equipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        // Validar datos básicos del equipo (por ejemplo, nombre obligatorio)
        if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del equipo es obligatorio.");
        }
        
        // Asignar el usuario autenticado como creador del equipo
        equipo.setCreador(currentUser);
        
        return equipoRepository.save(equipo);
    }
    
    /**
     * Obtiene la lista de equipos de un usuario.
     * 
     */
    public List<Equipo> getEquiposByUsuario(Long idUsuario) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        // Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));
        
        // Se asume que el repositorio dispone de un método para obtener equipos por creador
        return equipoRepository.findByCreador(usuario);
    }
    
    /**
     * Obtiene la lista de equipos de un usuario aplicando un filtro.
     * 
     */
    public List<Equipo> getEquiposByUsuarioWithFilter(Long idUsuario, String filtro) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        // Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));
        
        // Validar el filtro
        if (filtro == null || filtro.trim().isEmpty()) {
            throw new BadRequestException("Filtro inválido.");
        }
        
        return equipoRepository.findByCreadorAndNombreContainingIgnoreCase(usuario, filtro);
    }
    
    /**
     * Obtiene la lista de jugadores que juegan en un equipo.
     * 
     */
    public List<Jugador> getJugadoresByEquipo(Long idEquipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        // Verificar que el equipo exista
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));
        
        // consultamos el repositorio EquipoJugadores
        List<EquipoJugadores> relaciones = equipoJugadoresRepository.findById_Equipo_IdEquipo(equipo.getIdEquipo());

        
        // Mapear las relaciones a los objetos Jugador asociados.
        return relaciones.stream()
                .map(rel -> {
                    Long idJugador = rel.getId().getJugador().getIdJugador();
                    return jugadorRepository.findById(idJugador)
                            .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con id: " + idJugador));
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Añade un jugador a un equipo.
     * 
     */
    @Transactional
    public Jugador addJugadorToEquipo(Long idEquipo, Jugador jugador) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        // Verificar que el equipo exista
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));
        
        // Validar datos básicos del jugador
        if (jugador.getNombre() == null || jugador.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del jugador es obligatorio.");
        }
        
        // Crear y guardar el jugador
        Jugador nuevoJugador = jugadorRepository.save(jugador);
        
        // Crear la relación en la tabla de unión EquipoJugadores
        EquipoJugadores equipoJugador = new EquipoJugadores();
        EquipoJugadoresId id = new EquipoJugadoresId();
        id.setEquipo(equipo);
        id.setJugador(nuevoJugador);
        equipoJugador.setId(id);
        
        equipoJugadoresRepository.save(equipoJugador);
        
        return nuevoJugador;
    }
    
    /**
     * Elimina un equipo.
     * 
     */
    public void deleteEquipo(Long idEquipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));
        
        // Verificar permisos: solo el creador o un administrador pueden eliminar el equipo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !equipo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para eliminar este equipo");
        }
        
        equipoRepository.delete(equipo);
    }
}
