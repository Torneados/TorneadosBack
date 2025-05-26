package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.PartidoEquipos;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.TorneoEquipos;
import com.torneados.web.entities.TorneoJugadores;
import com.torneados.web.entities.Usuario;
import com.torneados.web.entities.ids.TorneoEquiposId;
import com.torneados.web.entities.ids.TorneoJugadoresId;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.repositories.EquipoRepository;
import com.torneados.web.repositories.JugadorRepository;
import com.torneados.web.repositories.PartidoEquiposRepository;
import com.torneados.web.repositories.TorneoEquiposRepository;
import com.torneados.web.repositories.TorneoJugadoresRepository;
import com.torneados.web.repositories.TorneoRepository;

@Service
public class TorneoEquiposService {

    private final TorneoEquiposRepository torneoEquiposRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final JugadorRepository jugadorRepository;
    private final TorneoJugadoresRepository torneoJugadoresRepository;
    private final PartidoEquiposRepository partidoEquiposRepository;
    private final AuthService authService;

    public TorneoEquiposService(TorneoEquiposRepository torneoEquiposRepository, 
                                TorneoRepository torneoRepository, 
                                EquipoRepository equipoRepository, 
                                JugadorRepository jugadorRepository,
                                TorneoJugadoresRepository torneoJugadoresRepository,
                                PartidoEquiposRepository partidoEquiposRepository,
                                AuthService authService) {
        this.torneoEquiposRepository = torneoEquiposRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.jugadorRepository = jugadorRepository;
        this.torneoJugadoresRepository = torneoJugadoresRepository;
        this.partidoEquiposRepository = partidoEquiposRepository;
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

        // Validar torneo y permisos
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        if (!torneo.getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para añadir equipos a este torneo.");
        }

        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado."));

        // Crear TorneoEquipos
        TorneoEquiposId torneoEquiposId = new TorneoEquiposId();
        torneoEquiposId.setTorneo(torneo);
        torneoEquiposId.setEquipo(equipo);

        TorneoEquipos torneoEquipos = new TorneoEquipos();
        torneoEquipos.setId(torneoEquiposId);
        torneoEquipos.setGolesContra(0);
        torneoEquipos.setGolesFavor(0);
        torneoEquipos.setPartidosGanados(0);
        torneoEquipos.setPartidosPerdidos(0);
        torneoEquipos.setPartidosEmpatados(0);
        torneoEquipos.setEliminado(false);
        torneoEquipos.setGrupo(null);

        torneoEquiposRepository.save(torneoEquipos);

        // 🚀 Inscribir todos los jugadores del equipo al torneo
        List<Jugador> jugadores = jugadorRepository.findByEquipoIdEquipo(idEquipo);
        for (Jugador jugador : jugadores) {
            TorneoJugadoresId id = new TorneoJugadoresId();
            id.setTorneo(torneo);
            id.setJugador(jugador);

            TorneoJugadores tj = new TorneoJugadores();
            tj.setId(id);
            tj.setPartidos(0);
            tj.setPuntos(0);
            tj.setTarjetasAmarillas(0);
            tj.setTarjetasRojas(0);

            torneoJugadoresRepository.save(tj);
        }

        return torneoEquipos;
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
        return torneoEquiposRepository.findByIdTorneoIdTorneo(idTorneo);
    }

    /**
     * Obtiene todos los equipos de un torneo que no han sido eliminados
     *
     * @param idTorneo El ID del torneo.
     * @return La lista de equipos del torneo que no han sido eliminados.
     */
    public List<TorneoEquipos> getAllEquiposByTorneoAndNotEliminados(Long idTorneo) {
        // Validar que el torneo existe
        torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));

        // Obtener todos los equipos del torneo que no han sido eliminados
        return torneoEquiposRepository.findByIdTorneoIdTorneoAndEliminadoFalse(idTorneo);
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
     * Actualiza los datos de un equipo en un torneo dado un objeto TorneoEquipos
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
     * Recalcula los datos de un equipo en un torneo
     * 
     * @param idTorneo El ID del torneo.
     * @param idEquipo El ID del equipo.
     * @param torneoEquipos Los nuevos datos del equipo en el torneo.
     * 
     * @return El torneo de equipos actualizado.
     */
    @Transactional
    public TorneoEquipos updateEquipoDataInTorneo(Long idTorneo, Long idEquipo) {
        // 1) Autenticación y permisos
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para actualizar un equipo en un torneo.");
        }
        Torneo torneo = torneoRepository.findById(idTorneo)
            .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permiso para actualizar los datos de este equipo en el torneo.");
        }

        // 2) Recuperar la relación TorneoEquipos
        Equipo equipo = equipoRepository.findById(idEquipo)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado."));
        TorneoEquiposId teId = new TorneoEquiposId();
        teId.setTorneo(torneo);
        teId.setEquipo(equipo);
        TorneoEquipos te = torneoEquiposRepository.findById(teId)
            .orElseThrow(() -> new BadRequestException("Relación entre torneo y equipo no es correcta."));

        // 3) Traer cada partido como par [yo, rival]
        //    findPartidosConRival devuelve List<Object[]> donde
        //    Object[0]=mi registro, Object[1]=rival
        List<Object[]> filas = partidoEquiposRepository.findPartidosConRival(idTorneo, idEquipo);

        // Si no hay filas, inicializamos a cero y devolvemos
        if (filas.isEmpty()) {
            te.setGolesFavor(0);
            te.setGolesContra(0);
            te.setPartidosGanados(0);
            te.setPartidosEmpatados(0);
            te.setPartidosPerdidos(0);
            return torneoEquiposRepository.save(te);
        }

        // 4) Recalcular totales
        int golesFavor  = 0;
        int golesContra = 0;
        int ganados     = 0;
        int empatados   = 0;
        int perdidos    = 0;

        for (Object[] pareja : filas) {
            PartidoEquipos yo    = (PartidoEquipos) pareja[0];
            PartidoEquipos rival = (PartidoEquipos) pareja[1];

            int gf = yo.getPuntos();
            int gc = rival.getPuntos();
            golesFavor  += gf;
            golesContra += gc;

            if      (gf >  gc) ganados++;
            else if (gf == gc) empatados++;
            else               perdidos++;
        }

        // 5) Asignar y guardar
        te.setGolesFavor(golesFavor);
        te.setGolesContra(golesContra);
        te.setPartidosGanados(ganados);
        te.setPartidosEmpatados(empatados);
        te.setPartidosPerdidos(perdidos);

        return torneoEquiposRepository.save(te);
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
