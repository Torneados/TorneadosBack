package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.TorneoJugadores;
import com.torneados.web.entities.Usuario;
import com.torneados.web.entities.ids.TorneoJugadoresId;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.repositories.JugadorRepository;
import com.torneados.web.repositories.TorneoJugadoresRepository;
import com.torneados.web.repositories.TorneoRepository;

@Service
public class TorneoJugadoresService {

    private final TorneoJugadoresRepository torneoJugadoresRepository;
    private final TorneoRepository torneoRepository;
    private final JugadorRepository jugadorRepository;
    private final AuthService authService;

    public TorneoJugadoresService(TorneoJugadoresRepository torneoJugadoresRepository, TorneoRepository torneoRepository, JugadorRepository jugadorRepository, AuthService authService) {
        this.torneoJugadoresRepository = torneoJugadoresRepository;
        this.torneoRepository = torneoRepository;
        this.jugadorRepository = jugadorRepository;
        this.authService = authService;
    }

    /**
     * Crea las estadisticas de un jugador en un torneo
     * 
     * @param idTorneo ID del torneo
     * @param idJugador ID del jugador
     * 
     * @return TorneoJugadores creado
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado
     * @throws BadRequestException Si los datos del torneo son inválidos
     * @throws ResourceNotFoundException Si el torneo o el jugador no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear el torneo
     */
    public TorneoJugadores createTorneoJugadores(Long idTorneo, Long idJugador) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        // Validar que el torneo existe y que el usuario tiene permiso para crear las estadisticas del torneo
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) 
            && !torneo.getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para crear estadisticas de este torneo.");
        }

        // Validar que el jugador existe
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado."));

        // Crear las estadisticas del jugador en el torneo
        TorneoJugadoresId torneoJugadoresId = new TorneoJugadoresId();
        torneoJugadoresId.setTorneo(torneo);
        torneoJugadoresId.setJugador(jugador);
        TorneoJugadores torneoJugadores = new TorneoJugadores();
        torneoJugadores.setId(torneoJugadoresId);
        return torneoJugadoresRepository.save(torneoJugadores);

    }

    /**
     * Obtiene las estadisticas de los jugadores en un torneo
     * 
     * @param idTorneo ID del torneo
     * 
     * @return Lista de TorneoJugadores
     * 
     * @throws ResourceNotFoundException Si el torneo no existe
     */
    public List<TorneoJugadores> getTorneoJugadores(Long idTorneo) {
        torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        return torneoJugadoresRepository.findByIdTorneoIdTorneo(idTorneo);
    }

    /**
     * Actualiza las estadisticas de un jugador en un torneo
     * 
     * @param idTorneo ID del torneo
     * @param idJugador ID del jugador
     * @param TorneoJugadores TorneoJugadores a actualizar
     * 
     * @return TorneoJugadores actualizado
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado
     * @throws BadRequestException Si los datos del torneo son inválidos
     * @throws ResourceNotFoundException Si el torneo o el jugador no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear el torneo
     * 
     */
    @Transactional
    public TorneoJugadores updateTorneoJugadores(Long idTorneo,Long idJugador,TorneoJugadores torneoJugadores) {
        // 1) Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        // 2) Validar permiso sobre el torneo
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !torneo.getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para modificar este torneo.");
        }

        // 3) Validar que el jugador existe
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado."));

        // 4) Cargar la entidad existente TorneoJugadores
        TorneoJugadoresId pk = new TorneoJugadoresId();
        pk.setTorneo(torneo);
        pk.setJugador(jugador);
        TorneoJugadores existente = torneoJugadoresRepository.findById(pk)
                .orElseThrow(() -> new ResourceNotFoundException("Estadísticas no encontradas."));

        // 5) Sumamos los valores del objeto recibido (deltas) a los actuales
        existente.setPartidos(
            existente.getPartidos() + torneoJugadores.getPartidos()
        );
        existente.setPuntos(
            existente.getPuntos() + torneoJugadores.getPuntos()
        );
        existente.setTarjetasAmarillas(
            existente.getTarjetasAmarillas() + torneoJugadores.getTarjetasAmarillas()
        );
        existente.setTarjetasRojas(
            existente.getTarjetasRojas() + torneoJugadores.getTarjetasRojas()
        );

        // 6) Guardar y devolver la entidad actualizada
        return torneoJugadoresRepository.save(existente);
    }


    /**
     * Elimina las estadisticas de un jugador en un torneo
     * 
     * @param idTorneo ID del torneo
     * @param idJugador ID del jugador
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado
     * @throws BadRequestException Si los datos del torneo son inválidos
     * @throws ResourceNotFoundException Si el torneo o el jugador no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear el torneo
     */
    public void deleteTorneoJugadores(Long idTorneo, Long idJugador) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        // Validar que el torneo existe y que el usuario tiene permiso para crear las estadisticas del torneo
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) 
            && !torneo.getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para crear estadisticas de este torneo.");
        }

        // Validar que el jugador existe
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado."));

        // Eliminar las estadisticas del jugador en el torneo
        TorneoJugadoresId torneoJugadoresId = new TorneoJugadoresId();
        torneoJugadoresId.setTorneo(torneo);
        torneoJugadoresId.setJugador(jugador);
        torneoJugadoresRepository.deleteById(torneoJugadoresId);
    }


    
}
