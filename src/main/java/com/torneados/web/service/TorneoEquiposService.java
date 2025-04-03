package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.TorneoEquipos;
import com.torneados.web.entities.Usuario;
import com.torneados.web.entities.ids.TorneoEquiposId;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.repositories.EquipoRepository;
import com.torneados.web.repositories.TorneoEquiposRepository;
import com.torneados.web.repositories.TorneoRepository;

@Service
public class TorneoEquiposService {

    private final TorneoEquiposRepository torneoEquiposRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final AuthService authService;

    public TorneoEquiposService(TorneoEquiposRepository torneoEquiposRepository, TorneoRepository torneoRepository, EquipoRepository equipoRepository, AuthService authService) {
        this.torneoEquiposRepository = torneoEquiposRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.authService = authService;
    }

    /**
     * Añade un equipo a un torneo
     *
     * @param idTorneo El ID del torneo al que se añadirá el equipo.
     * @param idEquipo El ID del equipo a añadir.
     * @return El torneo de equipos creado.
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws BadRequestException Si los datos del torneo son inválidos.
     */
    public TorneoEquipos addEquipoToTorneo(Long idTorneo, Long idEquipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para añadir un equipo a un torneo.");
        }

        // Validar que el torneo existe y que el usuario tiene permiso para añadir equipos
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        if (!torneo.getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para añadir equipos a este torneo.");
        }

        // Crear la relación entre el torneo y el equipo
        TorneoEquiposId torneoEquiposId = new TorneoEquiposId();
        torneoEquiposId.setTorneo(torneo);
        torneoEquiposId.setEquipo(equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado.")));

        TorneoEquipos torneoEquipos = new TorneoEquipos();
        torneoEquipos.setId(torneoEquiposId);
        torneoEquipos.setGolesContra(0);
        torneoEquipos.setGolesFavor(0);
        torneoEquipos.setPartidosGanados(0);
        torneoEquipos.setPartidosPerdidos(0);
        torneoEquipos.setPartidosEmpatados(0);
        

        return torneoEquiposRepository.save(torneoEquipos);
    }


    /**
     * Obtiene todos los equipos de un torneo
     *
     * @param idTorneo El ID del torneo.
     * @return La lista de equipos del torneo.
     */
    public List<TorneoEquipos> getAllEquiposByTorneo(Long idTorneo) {
        // Validar que el torneo existe
        torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));

        // Obtener todos los equipos del torneo
        return torneoEquiposRepository.findByTorneoId(idTorneo);
    }

    /**
     * Obtiene los datos de un equipo en un torneo
     * 
     * @param idTorneo El ID del torneo.
     * @param idEquipo El ID del equipo.
     * @return Los datos del equipo en el torneo.
     */
    public TorneoEquipos getEquipoById (Long idTorneo, Long idEquipo){
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado."));

        // Validar que el equipo pertenece al torneo
        TorneoEquiposId torneoEquiposId = new TorneoEquiposId();
        torneoEquiposId.setTorneo(torneo);
        torneoEquiposId.setEquipo(equipo);
        TorneoEquipos torneoEquipos = torneoEquiposRepository.findById(torneoEquiposId)
                .orElseThrow(() -> new BadRequestException("Relación entre torneo y equipo no es correcta."));

        // Obtener los datos del equipo en el torneo
        return torneoEquipos;
    }

    /**
     * Actualiza los datos de un equipo en un torneo
     * 
     * @param idTorneo El ID del torneo.
     * @param idEquipo El ID del equipo.
     * @param torneoEquipos Los nuevos datos del equipo en el torneo.
     * 
     * @return El torneo de equipos actualizado.
     */
    public TorneoEquipos updateEquipoInTorneo(Long idTorneo, Long idEquipo, TorneoEquipos torneoEquipos) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para actualizar un equipo en un torneo.");
        }

        // Validar que el torneo existe
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));

        // Validar que el usuario tiene permiso para actualizar los datos del equipo en el torneo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permiso para actualizar los datos de este equipo en el torneo.");
        }

        TorneoEquiposId torneoEquiposId = new TorneoEquiposId();
        torneoEquiposId.setTorneo(torneo);
        torneoEquiposId.setEquipo(equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado.")));

        // Obtener el torneoEquipos existente
        TorneoEquipos existingTorneoEquipos = torneoEquiposRepository.findById(torneoEquiposId)
                .orElseThrow(() -> new BadRequestException("Relación entre torneo y equipo no es correcta."));

        // Actualizar los datos del torneo de equipos
        existingTorneoEquipos.setGolesFavor(torneoEquipos.getGolesFavor());
        existingTorneoEquipos.setGolesContra(torneoEquipos.getGolesContra());
        existingTorneoEquipos.setPartidosGanados(torneoEquipos.getPartidosGanados());
        existingTorneoEquipos.setPartidosPerdidos(torneoEquipos.getPartidosPerdidos());
        existingTorneoEquipos.setPartidosEmpatados(torneoEquipos.getPartidosEmpatados());

        return torneoEquiposRepository.save(existingTorneoEquipos);
    }

    /**
     * Elimina un equipo de un torneo
     * 
     * @param idTorneo El ID del torneo.
     * @param idEquipo El ID del equipo.
     */
    public void deleteEquipoFromTorneo(Long idTorneo, Long idEquipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para eliminar un equipo de un torneo.");
        }

        // Validar que el torneo existe
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));

        // Validar que el usuario tiene permiso para eliminar el equipo del torneo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permiso para eliminar este equipo del torneo.");
        }

        // Eliminar la relación entre el torneo y el equipo
        TorneoEquiposId torneoEquiposId = new TorneoEquiposId();
        torneoEquiposId.setTorneo(torneo);
        torneoEquiposId.setEquipo(equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado.")));

        // Verificar si la relación existe antes de eliminar
        if(!torneoEquiposRepository.existsById(torneoEquiposId)) {
            throw new BadRequestException("Relación entre torneo y equipo no es correcta.");
        }

        torneoEquiposRepository.deleteById(torneoEquiposId);
    }

}
