package com.torneados.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Partido;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.TorneoEquipos;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.repositories.PartidoRepository; import com.torneados.web.repositories.TorneoRepository;

@Service 
public class PartidoService {
    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;
    private final PartidoEquiposService partidoEquiposService;
    private final JugadorService jugadorService;
    private final PartidoJugadoresService partidoJugadoresService;
    private final TorneoEquiposService torneoEquiposService;
    private final AuthService authService;

    public PartidoService(PartidoRepository partidoRepository, 
                        TorneoRepository torneoRepository,
                        PartidoEquiposService partidoEquiposService, 
                        JugadorService jugadorService ,
                        PartidoJugadoresService partidoJugadoresService ,
                        TorneoEquiposService torneoEquiposService,
                        AuthService authService) {
        this.partidoRepository = partidoRepository;
        this.torneoRepository = torneoRepository;
        this.partidoEquiposService = partidoEquiposService;
        this.jugadorService = jugadorService;
        this.partidoJugadoresService = partidoJugadoresService;
        this.torneoEquiposService = torneoEquiposService;
        this.authService = authService;
    }

    /**
     * Crea un nuevo partido.
     *
     * @param partido Objeto con los datos del partido a crear.
     * @param idTorneo ID del torneo al que pertenece el partido.
     * @return El partido creado.
     */
    public Partido createPartido(Long idTorneo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        // Verificar que el torneo exista
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con id: " + idTorneo));
        Partido partido = new Partido();
        partido.setTorneo(torneo);
        
        return partidoRepository.save(partido);
    }


    /**
     * Obtiene la lista de partidos asociados a un torneo.
     *
     * @param idTorneo ID del torneo.
     * @return Lista de partidos del torneo.
     * @throws ResourceNotFoundException Si el torneo no existe.
     */
    public List<Partido> getPartidosByTorneo(Long idTorneo) {
        // Validar que el torneo exista
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));
        
        return partidoRepository.findByTorneoIdTorneo(torneo.getIdTorneo());
    }

    /**
     * Obtiene un partido por su ID.
     *
     * @param id ID del partido.
     * @return El partido encontrado.
     * @throws ResourceNotFoundException Si el partido no existe.
     */
    public Partido getPartidoById(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
    }
    
    /**
     * Actualiza la fecha de un partido. de un torneo
     * 
     *
     * @param id ID del partido a actualizar.
     * @param partidoActualizado Objeto con las estadísticas actualizadas.
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     * @throws UnauthorizedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si el partido no existe.
     * @throws AccessDeniedException Si el usuario no tiene permisos para modificar.
     */
    @Transactional
    public void updatePartido(Long id, Partido partidoActualizado, Long idTorneo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        // Obtener el partido existente
        Partido partidoExistente = getPartidoById(id);

        // Verificar que el torneo del partido coincida con el torneo proporcionado
        if (!partidoExistente.getTorneo().getIdTorneo().equals(idTorneo)) {
            throw new BadRequestException("El partido no pertenece al torneo especificado.");
        }
        
        // Verificar permisos: solo ADMIN o el creador del torneo pueden modificar el partido
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) &&
            !partidoExistente.getTorneo().getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para modificar este partido.");
        }
        
        // Actualizar los datos del partido
        partidoExistente.setFechaComienzo(partidoActualizado.getFechaComienzo());
        
        partidoRepository.save(partidoExistente);
    }

    /**
     * Elimina un partido de un torneo.
     *
     * @param id ID del partido a eliminar.
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     * @throws UnauthorizedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si el partido no existe.
     * @throws AccessDeniedException Si el usuario no tiene permisos para eliminarlo.
     */
    @Transactional
    public void deletePartido(Long id, Long idTorneo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        // Obtener el partido existente
        Partido partidoExistente = getPartidoById(id);

        // Verificar que el torneo del partido coincida con el torneo proporcionado
        if (!partidoExistente.getTorneo().getIdTorneo().equals(idTorneo)) {
            throw new BadRequestException("El partido no pertenece al torneo especificado.");
        }

        // Verificar permisos: solo ADMIN o el creador del torneo pueden eliminar el partido
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) &&
            !partidoExistente.getTorneo().getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para eliminar este partido.");
        }
        
        // Eliminar el partido
        partidoRepository.delete(partidoExistente);
    }
    

    /* METODOS AUXILIARES PARA HACER SORTEO DE UN TORNEO */

    /**
     * 1. Reparte aleatoriamente los equipos en grupos (A, B, C…).
     * 2. Guarda el campo grupo en TorneoEquipos.
     * 3. Para cada grupo, genera un round-robin; si idaYVuelta==true genera también el partido de vuelta.
     */
    @Transactional
    public void crearGrupos(Torneo torneo, List<TorneoEquipos> inscritos, boolean idaYVuelta) {
        Collections.shuffle(inscritos);
        int n = inscritos.size();
        int numGrupos = calcularNumGrupos(n);

        // 1) asignar letra de grupo
        for (int i = 0; i < n; i++) {
            TorneoEquipos te = inscritos.get(i);
            String letra = String.valueOf((char)('A' + (i % numGrupos)));
            te.setGrupo(letra);
            TorneoEquipos patch = new TorneoEquipos();
            patch.setGrupo(letra);
            torneoEquiposService.updateEquipoInTorneo(
                torneo.getIdTorneo(),
                te.getId().getEquipo().getIdEquipo(),
                patch
            );
        }

        // 2) por cada grupo, round-robin
        Map<String, List<TorneoEquipos>> porGrupo =
            inscritos.stream().collect(Collectors.groupingBy(TorneoEquipos::getGrupo));
        for (List<TorneoEquipos> grupo : porGrupo.values()) {
            int m = grupo.size();
            for (int i = 0; i < m; i++) {
                for (int j = i + 1; j < m; j++) {
                    crearPartido(grupo.get(i), grupo.get(j), 0, idaYVuelta);
                }
            }
        }
    }

    /**
     * 1. Prepara un “bracket” de tamaño potencia de 2 (null = bye).
     * 2. Crea todos los partidos de todas las rondas “vacíos”.
     */
    @Transactional
    public void crearEliminatorias(Torneo torneo, List<TorneoEquipos> inscritos) {
        Collections.shuffle(inscritos);
        int n = inscritos.size(), pot2 = 1;
        while (pot2 < n) pot2 <<= 1;
        int rondas = (int)(Math.log(pot2) / Math.log(2));

        // bracket con null = bye
        List<TorneoEquipos> bracket = new ArrayList<>(inscritos);
        for (int i = n; i < pot2; i++) bracket.add(null);

        // ronda 1
        List<Partido> prev = new ArrayList<>();
        for (int i = 0; i < bracket.size(); i += 2) {
            prev.add(crearPartido(bracket.get(i), bracket.get(i+1), 1, false));
        }

        // rondas siguientes
        for (int r = 2; r <= rondas; r++) {
            List<Partido> next = new ArrayList<>();
            for (int i = 0; i < prev.size(); i += 2) {
                next.add(crearPartido(null, null, r, false));
            }
            prev = next;
        }
    }

    private int calcularNumGrupos(int n) {
        if (n < 4) throw new BadRequestException("Se necesitan al menos 4 equipos para fase de grupos.");
        if (n < 8) return 2;
        int tam = 4;
        while (tam > 2 && (n % tam) != 0) tam--;
        return (int)Math.ceil((double)n / tam);
    }

    /**
 * Crea un partido (ida + opcional vuelta) y sus stats de equipo+jugadores.
 */
private Partido crearPartido(TorneoEquipos te1, TorneoEquipos te2, int ronda, boolean esIdaYVuelta) {
    // 1) Construyo y guardo el partido de ida en una variable final
    Partido partidoBase = new Partido();
    partidoBase.setTorneo(te1 != null ? te1.getId().getTorneo() : te2.getId().getTorneo());
    partidoBase.setRonda(ronda);
    partidoBase.setFechaComienzo(null);
    final Partido partidoIda = partidoRepository.save(partidoBase);

    // 2) Estadísticas de equipos y jugadores para el partido de ida
    if (te1 != null) {
        partidoEquiposService.createPartidoEquipos(
            partidoIda.getIdPartido(),
            te1.getId().getEquipo().getIdEquipo(),
            1,
            true
        );
        jugadorService.getJugadoresByEquipo(te1.getId().getEquipo().getIdEquipo())
            .forEach(j -> partidoJugadoresService.createPartidoJugadores(
                partidoIda.getIdPartido(),
                j.getIdJugador(),
                1
            ));
    }
    if (te2 != null) {
        partidoEquiposService.createPartidoEquipos(
            partidoIda.getIdPartido(),
            te2.getId().getEquipo().getIdEquipo(),
            1,
            false
        );
        jugadorService.getJugadoresByEquipo(te2.getId().getEquipo().getIdEquipo())
            .forEach(j -> partidoJugadoresService.createPartidoJugadores(
                partidoIda.getIdPartido(),
                j.getIdJugador(),
                1
            ));
    }

    // 3) Partido de vuelta (solo si es ida y vuelta), también guardado en variable final
    if (esIdaYVuelta) {
        Partido partidoBaseVuelta = new Partido();
        partidoBaseVuelta.setTorneo(partidoIda.getTorneo());
        partidoBaseVuelta.setRonda(ronda);
        partidoBaseVuelta.setFechaComienzo(null);
        final Partido partidoVuelta = partidoRepository.save(partidoBaseVuelta);

        if (te2 != null) {
            partidoEquiposService.createPartidoEquipos(
                partidoVuelta.getIdPartido(),
                te2.getId().getEquipo().getIdEquipo(),
                1,
                true
            );
            jugadorService.getJugadoresByEquipo(te2.getId().getEquipo().getIdEquipo())
                .forEach(j -> partidoJugadoresService.createPartidoJugadores(
                    partidoVuelta.getIdPartido(),
                    j.getIdJugador(),
                    1
                ));
        }
        if (te1 != null) {
            partidoEquiposService.createPartidoEquipos(
                partidoVuelta.getIdPartido(),
                te1.getId().getEquipo().getIdEquipo(),
                1,
                false
            );
            jugadorService.getJugadoresByEquipo(te1.getId().getEquipo().getIdEquipo())
                .forEach(j -> partidoJugadoresService.createPartidoJugadores(
                    partidoVuelta.getIdPartido(),
                    j.getIdJugador(),
                    1
                ));
        }
    }

    // Devuelvo el partido de ida
    return partidoIda;
}



    
}

