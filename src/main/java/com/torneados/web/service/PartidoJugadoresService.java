package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.Partido;
import com.torneados.web.entities.PartidoJugadores;
import com.torneados.web.entities.Usuario;
import com.torneados.web.entities.ids.PartidoJugadoresId;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.repositories.JugadorRepository;
import com.torneados.web.repositories.PartidoJugadoresRepository;
import com.torneados.web.repositories.PartidoRepository;

@Service
public class PartidoJugadoresService {
    
    private final PartidoJugadoresRepository partidoJugadoresRepository;
    private final PartidoRepository partidoRepository;
    private final JugadorRepository jugadorRepository;
    private final AuthService authService;

    public PartidoJugadoresService(PartidoJugadoresRepository partidoJugadoresRepository, PartidoRepository partidoRepository, JugadorRepository jugadorRepository, AuthService authService) {
        this.partidoJugadoresRepository = partidoJugadoresRepository;
        this.partidoRepository = partidoRepository;
        this.jugadorRepository = jugadorRepository;
        this.authService = authService;
    }

    /**
     * Crea las estadisticas de un jugador en un partido
     * 
     * @param idPartido ID del partido
     * @param idJugador ID del jugador
     * 
     * @return PartidoJugadores creado
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado
     * @throws BadRequestException Si los datos del partido son inválidos
     * @throws ResourceNotFoundException Si el partido o el jugador no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear el partido
     * 
     */
    public PartidoJugadores createPartidoJugadores(Long idPartido, Long idJugador) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        // Validar que el partido existe y que el usuario tiene permiso para crear las estadisticas del partido
        Partido partido = partidoRepository.findById(idPartido)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) 
            && !partido.getTorneo().getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para crear estadisticas de este partido.");
        }

        // Validar que el jugador existe
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado."));

        // Crear la estadistica del jugador en el partido
        PartidoJugadoresId partidoJugadoresId = new PartidoJugadoresId();
        partidoJugadoresId.setPartido(partido);
        partidoJugadoresId.setJugador(jugador);
        partidoJugadoresId.setNumSet(1); // Asignar el primer set por defecto
        PartidoJugadores partidoJugadores = new PartidoJugadores();
        partidoJugadores.setId(partidoJugadoresId);
        return partidoJugadoresRepository.save(partidoJugadores);
    }

    /**
     * Obtiene las estadisticas de los jugadores de un partido
     * 
     * @param idPartido ID del partido
     * 
     * @return Lista de estadisticas de los jugadores en el partido
     * 
     * @throws ResourceNotFoundException Si el partido no existe
     * 
     */
    public List<PartidoJugadores> getPartidoJugadores(Long idPartido) {
        partidoRepository.findById(idPartido)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado."));
        return partidoJugadoresRepository.findByIdPartidoIdPartido(idPartido);
    }

    /**
     * Actualiza las estadisticas de un jugador en un partido
     * 
     * @param idPartido ID del partido
     * @param idJugador ID del jugador
     * @param partidoJugadores Objeto PartidoJugadores con los datos a actualizar
     * 
     * @return PartidoJugadores actualizado
     * 
     * @throws ResourceNotFoundException Si el partido o el jugador no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para actualizar las estadisticas
     * @throws BadRequestException Si los datos del partido son inválidos
     * @throws UnauthorizedException Si el usuario no está autenticado 
     */
    public PartidoJugadores updatePartidoJugadores(Long idPartido, Long idJugador, PartidoJugadores partidoJugadores) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        // Validar que el partido existe y que el usuario tiene permiso para actualizar las estadisticas del partido
        Partido partido = partidoRepository.findById(idPartido)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) 
            && !partido.getTorneo().getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para actualizar estadisticas de este partido.");
        }

        // Validar que el jugador existe
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado."));

        // Actualizar la estadistica del jugador en el partido
        PartidoJugadoresId partidoJugadoresId = new PartidoJugadoresId();
        partidoJugadoresId.setPartido(partido);
        partidoJugadoresId.setJugador(jugador);
        partidoJugadoresId.setNumSet(partidoJugadores.getId().getNumSet()); // Asignar el set correspondiente
        partidoJugadoresRepository.findById(partidoJugadoresId)
                .orElseThrow(() -> new ResourceNotFoundException("Estadisticas del jugador no encontradas."));
        
        return partidoJugadoresRepository.save(partidoJugadores);
    }

    /**
     * Elimina las estadisticas de un jugador en un partido
     * 
     * @param idPartido ID del partido
     * @param idJugador ID del jugador
     * 
     * @throws ResourceNotFoundException Si el partido o el jugador no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para eliminar las estadisticas
     * @throws BadRequestException Si los datos del partido son inválidos
     * @throws UnauthorizedException Si el usuario no está autenticado 
     */
    public void deletePartidoJugadores(Long idPartido, Long idJugador) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        // Validar que el partido existe y que el usuario tiene permiso para eliminar las estadisticas del partido
        Partido partido = partidoRepository.findById(idPartido)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) 
            && !partido.getTorneo().getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para eliminar estadisticas de este partido.");
        }

        // Validar que el jugador existe
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado."));

        // Eliminar la estadistica del jugador en el partido
        PartidoJugadoresId partidoJugadoresId = new PartidoJugadoresId();
        partidoJugadoresId.setPartido(partido);
        partidoJugadoresId.setJugador(jugador);
        partidoJugadoresRepository.deleteById(partidoJugadoresId);
    }
}
