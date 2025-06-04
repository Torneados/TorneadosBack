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
import com.torneados.web.repositories.JugadorRepository;
import com.torneados.web.repositories.PartidoRepository; import com.torneados.web.repositories.TorneoRepository;

@Service 
public class PartidoService {
    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;
    private final PartidoEquiposService partidoEquiposService;
    private final JugadorRepository jugadorRepository;
    private final PartidoJugadoresService partidoJugadoresService;
    private final TorneoEquiposService torneoEquiposService;
    private final AuthService authService;

    public PartidoService(PartidoRepository partidoRepository, 
                        TorneoRepository torneoRepository,
                        PartidoEquiposService partidoEquiposService, 
                        JugadorRepository jugadorRepository,
                        PartidoJugadoresService partidoJugadoresService ,
                        TorneoEquiposService torneoEquiposService,
                        AuthService authService) {
        this.partidoRepository = partidoRepository;
        this.torneoRepository = torneoRepository;
        this.partidoEquiposService = partidoEquiposService;
        this.jugadorRepository = jugadorRepository;
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

        // 2) round-robin por grupo con jornadas
        Map<String, List<TorneoEquipos>> porGrupo =
            inscritos.stream().collect(Collectors.groupingBy(TorneoEquipos::getGrupo));

        for (List<TorneoEquipos> grupo : porGrupo.values()) {
            List<List<int[]>> jornadas = generarRoundRobin(grupo.size());

            for (int j = 0; j < jornadas.size(); j++) {
                for (int[] par : jornadas.get(j)) {
                    crearPartido(grupo.get(par[0]), grupo.get(par[1]), j + 1, null);
                }
            }

            if (idaYVuelta) {
                int offset = jornadas.size();
                for (int j = 0; j < jornadas.size(); j++) {
                    for (int[] par : jornadas.get(j)) {
                        crearPartido(grupo.get(par[1]), grupo.get(par[0]), offset + j + 1, null);
                    }
                }
            }
        }
    }


    @Transactional
    public void crearLiga(Torneo torneo, List<TorneoEquipos> inscritos, boolean idaYVuelta) {
        Collections.shuffle(inscritos);

        // 1) Asignar todos al grupo "A"
        for (TorneoEquipos te : inscritos) {
            te.setGrupo("A");
            TorneoEquipos patch = new TorneoEquipos();
            patch.setGrupo("A");
            torneoEquiposService.updateEquipoInTorneo(
                torneo.getIdTorneo(),
                te.getId().getEquipo().getIdEquipo(),
                patch
            );
        }

        // 2) Round-robin con jornadas
        List<List<int[]>> jornadas = generarRoundRobin(inscritos.size());

        for (int j = 0; j < jornadas.size(); j++) {
            for (int[] par : jornadas.get(j)) {
                crearPartido(inscritos.get(par[0]), inscritos.get(par[1]), j + 1, null);
            }
        }

        if (idaYVuelta) {
            int offset = jornadas.size();
            for (int j = 0; j < jornadas.size(); j++) {
                for (int[] par : jornadas.get(j)) {
                    crearPartido(inscritos.get(par[1]), inscritos.get(par[0]), offset + j + 1, null);
                }
            }
        }
    }

    /**
     * Crea todo el bracket eliminatorio para un torneo: ronda 1 (con emparejamientos te1 vs te2
     * y posibles “bye” si es impar) y luego todas las rondas posteriores con partidos “vacíos”
     * (sin asignar te1/te2), dejando únicamente el campo 'ronda' y el torneo.
     *
     * @param torneo     Torneo sobre el que se generan las eliminatorias.
     * @param inscritos  Lista de TorneoEquipos inscritos (no eliminados).
     */
    @Transactional
    public void crearEliminatorias(Torneo torneo, List<TorneoEquipos> inscritos) {
        int n = inscritos.size();
        // Potencia de dos >= n
        int pot2 = 1;
        while (pot2 < n) pot2 <<= 1;
        // Número de rondas necesarias: log2(pot2)
        int rondas = (int)(Math.log(pot2) / Math.log(2));

        List<TorneoEquipos> bracket = new ArrayList<>();

        // 1) Si venimos de fase Liga o Grupos, emparejamos por posición
        if (torneo.isLiga() || torneo.isGrupos()) {
            // Se asume que 'inscritos' ya viene ordenado por clasificación (1º, 2º, …).
            for (int i = 0; i < n / 2; i++) {
                bracket.add(inscritos.get(i));               // mejor clasificado
                bracket.add(inscritos.get(n - 1 - i));       // peor clasificado
            }
            // Si la cantidad es impar, el que queda en el medio pasa directo (“bye”)
            if (n % 2 != 0) {
                bracket.add(inscritos.get(n / 2));
            }
        } else {
            // 2) Si es eliminatoria directa sin fase previa: mezcla aleatoria
            Collections.shuffle(inscritos);
            bracket.addAll(inscritos);
        }

        // 3) Rellenar con nulls hasta completar pot2: así indicamos “byes” sobrantes
        while (bracket.size() < pot2) {
            bracket.add(null);
        }

        // ===== RONDA 1 =====
        // Creamos los partidos de la primera ronda:  bracket[0] vs bracket[1], bracket[2] vs bracket[3], …
        List<Partido> rondasPrevias = new ArrayList<>();
        for (int i = 0; i < bracket.size(); i += 2) {
            TorneoEquipos te1 = bracket.get(i);         // puede ser null si bye
            TorneoEquipos te2 = bracket.get(i + 1);     // puede ser null si bye
            Partido partidoRonda1 = crearPartido(
                torneo, te1, te2,
                /*jornada=*/ null,
                /*ronda=*/ 1
            );
            rondasPrevias.add(partidoRonda1);
        }

        // ===== RONDAS SIGUIENTES =====
        // Para cada ronda r = 2..rondas, creamos partidos sin equipos asignados
        for (int r = 2; r <= rondas; r++) {
            List<Partido> siguientes = new ArrayList<>();
            /*
             * Cada dos partidos previos se emparejan para generar uno en la siguiente ronda.
             * Pero en este punto no conocemos aún los ganadores, así que creamos partidos “vacíos”
             * indicando solo la ronda y el torneo. Más adelante habrá que asignarles te1/te2.
             */
            for (int i = 0; i < rondasPrevias.size(); i += 2) {
                // te1=null y te2=null: partido placeholder de ronda r
                Partido partidoRondaR = crearPartido(
                    torneo,
                    /*te1=*/ null,
                    /*te2=*/ null,
                    /*jornada=*/ null,
                    /*ronda=*/ r
                );
                siguientes.add(partidoRondaR);
            }
            // Preparamos para la siguiente iteración
            rondasPrevias = siguientes;
        }
    }



    private Partido crearPartido(TorneoEquipos te1,TorneoEquipos te2,Integer jornada, Integer ronda) {
        // Obtenemos el torneo a partir de te1 o te2, si alguno es no-null.
        Torneo torneo;
        if (te1 != null) {
            torneo = te1.getId().getTorneo();
        } else if (te2 != null) {
            torneo = te2.getId().getTorneo();
        } else {
            throw new IllegalArgumentException(
                "No se puede crear un partido sin Torneo: te1 y te2 son ambos null"
            );
        }
        return crearPartido(torneo, te1, te2, jornada, ronda);
    }

    /**
     * crea un Partido en la base de datos
     * con el Torneo (obligatorio), la ronda y (opcionalmente) te1/te2.
     * Si te1 o te2 son null, el partido queda creado sin equipos (para rondas posteriores).
     *
     * @param torneo   El torneo al que pertenece este partido (jamás debe ser null).
     * @param te1      TorneoEquipos del equipo local o null si no hay equipo asignado.
     * @param te2      TorneoEquipos del equipo visitante o null si no hay equipo asignado.
     * @param jornada  Número de jornada (puede quedar null si no aplica).
     * @param ronda    Número de ronda eliminatoria (1 = primera ronda, 2 = siguiente, etc.).
     * @return Partido recién guardado en BD.
     */
    private Partido crearPartido(Torneo torneo,TorneoEquipos te1,TorneoEquipos te2,Integer jornada,Integer ronda) {
        // 1) Construir y guardar la entidad Partido
        Partido partido = new Partido();
        partido.setTorneo(torneo);
        partido.setJornada(jornada);
        partido.setRonda(ronda != null ? ronda : 0);
        partido.setFechaComienzo(null); // si quieres dejarla en null inicialmente

        // Al usar @Transactional en el método superior, la entidad queda “managed” tras save()
        final Partido pGuardado = partidoRepository.save(partido);

        // 2) Si hay equipo local (te1), crear relaciones PartidoEquipos y PartidoJugadores
        if (te1 != null) {
            Long idEquipo1 = te1.getId().getEquipo().getIdEquipo();
            partidoEquiposService.createPartidoEquipos(
                pGuardado.getIdPartido(),
                idEquipo1,
                /*numSet=*/ 1,
                /*esLocal=*/ true
            );
            // También ligamos los jugadores de ese equipo
            jugadorRepository.findByEquipoIdEquipo(idEquipo1)
                .forEach(j ->
                    partidoJugadoresService.createPartidoJugadores(
                        pGuardado.getIdPartido(),
                        j.getIdJugador(),
                        /*numSet=*/ 1
                    )
                );
        }

        // 3) Si hay equipo visitante (te2), creamos PartidoEquipos y PartidoJugadores
        if (te2 != null) {
            Long idEquipo2 = te2.getId().getEquipo().getIdEquipo();
            partidoEquiposService.createPartidoEquipos(
                pGuardado.getIdPartido(),
                idEquipo2,
                /*numSet=*/ 1,
                /*esLocal=*/ false
            );
            jugadorRepository.findByEquipoIdEquipo(idEquipo2)
                .forEach(j ->
                    partidoJugadoresService.createPartidoJugadores(
                        pGuardado.getIdPartido(),
                        j.getIdJugador(),
                        /*numSet=*/ 1
                    )
                );
        }

        return pGuardado;
    }


    private List<List<int[]>> generarRoundRobin(int n) {
        List<List<int[]>> jornadas = new ArrayList<>();
        boolean impar = (n % 2 != 0);
        int total = impar ? n + 1 : n;
        int[] equipos = new int[total];
        for (int i = 0; i < total; i++) equipos[i] = i;

        for (int r = 0; r < total - 1; r++) {
            List<int[]> jornada = new ArrayList<>();
            for (int i = 0; i < total / 2; i++) {
                int e1 = equipos[i], e2 = equipos[total - 1 - i];
                if (e1 < n && e2 < n) {
                    jornada.add(new int[]{e1, e2});
                }
            }
            jornadas.add(jornada);
            // rotación Berger
            int temp = equipos[1];
            System.arraycopy(equipos, 2, equipos, 1, total - 2);
            equipos[total - 1] = temp;
        }

        return jornadas;
    }

    private int calcularNumGrupos(int n) {
        if (n < 4) throw new BadRequestException("Se necesitan al menos 4 equipos para fase de grupos.");
        if (n < 8) return 2;
        int tam = 4;
        while (tam > 2 && (n % tam) != 0) tam--;
        return (int)Math.ceil((double)n / tam);
    }





    
}

