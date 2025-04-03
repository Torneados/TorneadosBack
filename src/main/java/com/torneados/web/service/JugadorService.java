package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.repositories.EquipoRepository;
import com.torneados.web.repositories.JugadorRepository;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final EquipoRepository equipoRepository;
    private final AuthService authService;
    
    public JugadorService(JugadorRepository jugadorRepository, EquipoRepository equipoRepository, AuthService authService) {
        this.jugadorRepository = jugadorRepository;
        this.equipoRepository = equipoRepository;
        this.authService = authService;
    }
    
    /**
     * Crea un nuevo jugador.
     * 
     * @param jugador Objeto con los datos del jugador a crear.
     * @param idEquipo ID del equipo al que pertenece el jugador.
     * @return El jugador creado.
     * @throws UnauthorizedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si no se encuentra el equipo.
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     */
    public Jugador createJugador(Jugador jugador, Long idEquipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        // Verificar que el equipo exista
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));

        jugador.setEquipo(equipo);

        return jugadorRepository.save(jugador);
    }

    /**
     * Obtiene todos los jugadores que juegan en un equipo.
     * 
     * @param idEquipo ID del equipo.
     * @return Lista de jugadores.
     */
    public List<Jugador> getJugadoresByEquipo(Long idEquipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        // Verificar que el equipo exista
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        
        // Verificar permisos: solo el creador o un administrador pueden ver los jugadores de un equipo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !equipo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para ver los jugadores de este equipo");
        }
        
        return jugadorRepository.findByEquipoIdEquipo(idEquipo);
    }

    /**
     * Obtiene un jugador por su ID.
     * 
     * @param id ID del jugador.
     * @return El jugador encontrado.
     * @throws ResourceNotFoundException Si no se encuentra el jugador.
     */
    public Jugador getJugadorById(Long id) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado "));
    }

    /**
     * Actualiza los datos de un jugador.
     * 
     * @param id ID del jugador a actualizar.
     * @param jugadorActualizado Objeto con los datos actualizados.
     * @return El jugador actualizado.
     * 
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     * @throws AccessDeniedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si no se encuentra el jugador.
     */
    @Transactional
    public Jugador updateJugador(Long id, Jugador jugadorActualizado, Long idEquipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }

        Jugador jugador = getJugadorById(id);
        
        // Verificar que el jugador pertenezca al equipo
        if (!jugador.getEquipo().getIdEquipo().equals(idEquipo)) {
            throw new BadRequestException("El jugador no pertenece al equipo con id");
        }
        // Verificar permisos: solo el creador o un administrador pueden actualizar los jugadores de un equipo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !jugador.getEquipo().getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para actualizar este jugador");
        }

        // Actualizar campos, validando que no sean nulos o vacíos
        if (jugadorActualizado.getNombre() == null || jugadorActualizado.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del jugador es obligatorio.");
        }
        jugador.setNombre(jugadorActualizado.getNombre());
        
        if (jugadorActualizado.getDni() == null || jugadorActualizado.getDni().trim().isEmpty()) {
            throw new BadRequestException("El DNI del jugador es obligatorio.");
        }
        jugador.setDni(jugadorActualizado.getDni());
        
        if (jugadorActualizado.getFechaNacimiento() != null) {
            jugador.setFechaNacimiento(jugadorActualizado.getFechaNacimiento());
        }
        
        return jugadorRepository.save(jugador);
    }
    
    /**
     * Elimina un jugador por su ID.
     * 
     * @param id ID del jugador a eliminar.
     * @throws AccessDeniedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si no se encuentra el jugador.
     */
    @Transactional
    public void deleteJugador(Long id, long idEquipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }

        Jugador jugador = getJugadorById(id);
    
        // Verificar que el jugador pertenezca al equipo
        if (!jugador.getEquipo().getIdEquipo().equals(idEquipo)) {
            throw new BadRequestException("El jugador no pertenece al equipo con id");
        }
        
        // Verificar permisos: solo el creador o un administrador pueden eliminar los jugadores de un equipo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !jugador.getEquipo().getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para eliminar este jugador");
        }

        jugadorRepository.delete(jugador);
    }
}
