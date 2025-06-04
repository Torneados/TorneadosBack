package com.torneados.web.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.*;
import com.torneados.web.entities.SolicitudInscripcion.Estado;
import com.torneados.web.entities.ids.SolicitudInscripcionId;
import com.torneados.web.exceptions.*;
import com.torneados.web.repositories.*;

@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final AuthService authService;
    private final SolicitudInscripcionRepository solicitudInscripcionRepository;
    private final TorneoEquiposService torneoEquiposService;
    private final TorneoJugadoresService torneoJugadoresService;
    private final JugadorRepository jugadorRepository;
    private final PartidoService partidoService;

    public TorneoService(TorneoRepository torneoRepository, AuthService authService,
                         SolicitudInscripcionRepository solicitudInscripcionRepository,
                         TorneoEquiposService torneoEquiposService,
                         TorneoJugadoresService torneoJugadoresService,
                         JugadorRepository jugadorRepository,
                         PartidoService partidoService) {
        this.torneoRepository = torneoRepository;
        this.authService = authService;
        this.solicitudInscripcionRepository = solicitudInscripcionRepository;
        this.torneoEquiposService = torneoEquiposService;
        this.torneoJugadoresService = torneoJugadoresService;
        this.jugadorRepository = jugadorRepository;
        this.partidoService = partidoService;
    }

    /**
     * Crea un nuevo torneo, validando los datos y asignando el creador.
     *
     * @param torneo El torneo a crear.
     * @return El torneo creado.
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws BadRequestException Si los datos del torneo son inválidos.
     */
    public Torneo createTorneo(Torneo torneo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para crear un torneo.");
        }

        if (torneo.getFechaFin().isBefore(torneo.getFechaComienzo())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la de comienzo.");
        }
        if (torneo.getEnlaceInstagram() != null && !torneo.getEnlaceInstagram().equals("") && !torneo.getEnlaceInstagram().contains("instagram.com")) {
            throw new BadRequestException("El enlace de Instagram no parece válido.");
        }
        if (torneo.getEnlaceFacebook() != null && !torneo.getEnlaceFacebook().equals("") && !torneo.getEnlaceFacebook().contains("facebook.com")) {
            throw new BadRequestException("El enlace de Facebook no parece válido.");
        }
        if (torneo.getEnlaceTwitter() != null && !torneo.getEnlaceTwitter().equals("") && !torneo.getEnlaceTwitter().contains("twitter.com")) {
            throw new BadRequestException("El enlace de Twitter no parece válido.");
        }

        torneo.setCreador(currentUser);
        return torneoRepository.save(torneo);
    }

    /**
     * Obtiene una lista con todos los torneos.
     *
     * @return La lista de torneos encontrados.
     */
    public List<Torneo> getAllTorneos() {
        return torneoRepository.findAll();
    }

    /**
     * Devuelve todos los torneos paginados, sin aplicar filtros.
     *
     * @param pageable Objeto Pageable (page, size, etc).
     * @return Página de torneos.
     */
    public Page<Torneo> getAllTorneos(Pageable pageable) {
        return torneoRepository.findAll(pageable);
    }

    /**
     * Devuelve torneos filtrados por nombre, lugar y/o deporte, y paginados.
     *
     * @param filtroNombre  Cadena para filtrar por nombre (puede estar vacía).
     * @param filtroLugar   Cadena para filtrar por lugar (puede estar vacía o nula).
     * @param filtroDeporte Cadena para filtrar por deporte (puede estar vacía o nula).
     * @param pageable      Objeto Pageable con page/size.
     * @return Página de torneos que cumplan esos filtros.
     */
    public Page<Torneo> getTorneosFiltrados(
            String filtroNombre,
            String filtroLugar,
            String filtroDeporte,
            Pageable pageable
    ) {
        boolean filtraLugar = filtroLugar != null && !filtroLugar.isEmpty();
        boolean filtraDeporte = filtroDeporte != null && !filtroDeporte.isEmpty();

        // Normalizamos filtros nulos a cadena vacía
        String nom = (filtroNombre != null ? filtroNombre : "");
        String lug = (filtroLugar  != null ? filtroLugar  : "");
        String dep = (filtroDeporte != null ? filtroDeporte : "");

        if (!filtraLugar && !filtraDeporte) {
            // Solo filtrar por nombre
            return torneoRepository.findByNombreContainingIgnoreCase(nom, pageable);

        } else if (filtraLugar && !filtraDeporte) {
            // Filtrar por nombre + lugar
            return torneoRepository.findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCase(
                    nom, lug, pageable
            );

        } else if (!filtraLugar && filtraDeporte) {
            // Filtrar por nombre + deporte
            return torneoRepository.findByNombreContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(
                    nom, dep, pageable
            );

        } else {
            // Filtrar por nombre + lugar + deporte
            return torneoRepository.findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(
                    nom, lug, dep, pageable
            );
        }
    }

    /**
     * Obtiene un torneo por su ID (sin paginar, porque es una única entidad).
     */
    public Torneo getTorneoById(Long id) {
        return torneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));
    }

    /**
     * Actualiza un torneo, validando si el usuario autenticado tiene permisos para modificarlo.
     *
     * @param id ID del torneo a actualizar.
     * @param torneo Datos del torneo a actualizar.
     * @return El torneo actualizado.
     * @throws AccessDeniedException Si el usuario no tiene permisos para modificar.
     * @throws ResourceNotFoundException Si el torneo no existe.
     */
    public Torneo updateTorneo(Long id, Torneo torneo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para modificar un torneo.");
        }

        Torneo existente = torneoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !existente.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para modificar este torneo");
        }

        // Validación de fechas
        if (torneo.getFechaComienzo() != null && torneo.getFechaFin() != null) {
            if (torneo.getFechaFin().isBefore(torneo.getFechaComienzo())) {
                throw new BadRequestException("La fecha de fin no puede ser anterior a la de comienzo.");
            }
        }

        // Validación de enlaces
        if (torneo.getEnlaceInstagram() != null && !torneo.getEnlaceInstagram().contains("instagram.com")) {
            throw new BadRequestException("El enlace de Instagram no parece válido.");
        }
        if (torneo.getEnlaceFacebook() != null && !torneo.getEnlaceFacebook().contains("facebook.com")) {
            throw new BadRequestException("El enlace de Facebook no parece válido.");
        }
        if (torneo.getEnlaceTwitter() != null && !torneo.getEnlaceTwitter().contains("twitter.com")) {
            throw new BadRequestException("El enlace de Twitter no parece válido.");
        }

        // Actualizar TODOS los campos editables
        existente.setNombre(torneo.getNombre());
        existente.setDescripcion(torneo.getDescripcion());
        existente.setEsPublico(torneo.isEsPublico());
        existente.setLugar(torneo.getLugar());
        existente.setDeporte(torneo.getDeporte());
        existente.setLiga(torneo.isLiga());
        existente.setIdaYVuelta(torneo.isIdaYVuelta());
        existente.setGrupos(torneo.isGrupos());
        existente.setEliminatoria(torneo.isEliminatoria());
        existente.setFechaComienzo(torneo.getFechaComienzo());
        existente.setFechaFin(torneo.getFechaFin());
        existente.setContactoEmail(torneo.getContactoEmail());
        existente.setContactoTelefono(torneo.getContactoTelefono());
        existente.setEnlaceInstagram(torneo.getEnlaceInstagram());
        existente.setEnlaceFacebook(torneo.getEnlaceFacebook());
        existente.setEnlaceTwitter(torneo.getEnlaceTwitter());
        // NOTA: el campo 'fase' lo gestionan tus endpoints de sorteo, no se toca aquí

        return torneoRepository.save(existente);
    }

    /**
     * Actualiza únicamente la fase de un torneo.
     * Comprueba autenticación y permisos, carga la entidad, asigna la nueva fase y guarda.
     *
     * @param idTorneo  ID del torneo.
     * @param nuevaFase Nueva fase a asignar (0, 1 o 2).
     * @throws UnauthorizedException   Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si el torneo no existe.
     * @throws AccessDeniedException    Si el usuario no es ADMIN ni creador.
     * @throws BadRequestException      Si la fase es negativa.
     */
    @Transactional
    public void actualizarFase(Long idTorneo, int nuevaFase) {
        if (nuevaFase < 0) {
            throw new BadRequestException("Fase inválida: debe ser un valor no negativo");
        }

        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para modificar la fase del torneo.");
        }

        Torneo torneo = torneoRepository.findById(idTorneo)
            .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + idTorneo));

        boolean isCreador = torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario());
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) && !isCreador) {
            throw new AccessDeniedException("No tienes permisos para actualizar la fase de este torneo.");
        }

        torneo.setFase(nuevaFase);
        // Al estar en contexto @Transactional y ser 'torneo' una entidad managed,
        // se guardará automáticamente al finalizar el método.
    }


    /**
     * Borra un torneo, validando si el usuario autenticado tiene permisos para eliminarlo.
     *
     * @param id ID del torneo a eliminar.
     * @throws AccessDeniedException Si el usuario no tiene permisos para eliminar.
     * @throws ResourceNotFoundException Si el torneo no existe.
     */
    public void deleteTorneo(Long id) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para eliminar un torneo.");
        }

        if (id == null || id <= 0) {
            throw new BadRequestException("ID de torneo inválido.");
        }

        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + id));

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para eliminar este torneo");
        }

        torneoRepository.delete(torneo);
    }

    /**
     * Obtiene una lista de solicitudes de inscripción para un torneo específico.
     *
     * @param idTorneo ID del torneo.
     * @return Lista de solicitudes de inscripción.
     * @throws ResourceNotFoundException Si el torneo no existe.
     */
    public List<SolicitudInscripcion> getSolicitudesInscripcion(Long idTorneo) {
        Torneo torneo = torneoRepository.findById(idTorneo)
            .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + idTorneo));

        return solicitudInscripcionRepository.findByIdTorneo(torneo);
    }

    /**
     * Cambia el estado de una solicitud de inscripción.
     * Si se acepta, el equipo es añadido al torneo automáticamente.
     *
     * @param idSolicitud ID compuesto de la solicitud.
     * @param nuevoEstado Nuevo estado a asignar.
     * @throws UnauthorizedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si la solicitud no existe.
     * @throws AccessDeniedException Si el usuario no tiene permisos.
     * @throws BadRequestException Si el equipo ya está inscrito y se intenta aceptar de nuevo.
     */
    public void cambiarEstadoSolicitudInscripcion(SolicitudInscripcionId idSolicitud, Estado nuevoEstado) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para modificar una solicitud.");
        }

        SolicitudInscripcion solicitud = solicitudInscripcionRepository.findById(idSolicitud)
            .orElseThrow(() -> new ResourceNotFoundException("Solicitud de inscripción no encontrada"));

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !solicitud.getId().getTorneo().getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para modificar esta solicitud");
        }

        solicitud.setEstado(nuevoEstado);
        solicitudInscripcionRepository.save(solicitud);

        if (nuevoEstado == Estado.ACEPTADA) {
            torneoEquiposService.addEquipoToTorneo(
                solicitud.getId().getTorneo().getIdTorneo(),
                solicitud.getId().getEquipo().getIdEquipo()
            );
            List<Jugador> jugadores = jugadorRepository.findByEquipoIdEquipo(solicitud.getId().getEquipo().getIdEquipo());
            for (Jugador jugador : jugadores) {
                torneoJugadoresService.createTorneoJugadores(
                    solicitud.getId().getTorneo().getIdTorneo(),
                    jugador.getIdJugador()
                );
            }

        
        }
    }

    /**
     * Permite a un equipo solicitar la inscripción a un torneo.
     *
     * @param idTorneo ID del torneo al que se desea inscribir.
     * @param equipo El equipo que solicita la inscripción.
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws BadRequestException Si ya existe una solicitud previa.
     * @throws ResourceNotFoundException Si el torneo no existe.
     */
    public void solicitarInscripcion(Long idTorneo, Equipo equipo) {
        Usuario currentUser = authService.getAuthenticatedUser();

        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para inscribirte en un torneo.");
        }

        Torneo torneo = torneoRepository.findById(idTorneo)
            .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + idTorneo));

        SolicitudInscripcionId solicitudId = new SolicitudInscripcionId();
        solicitudId.setTorneo(torneo);
        solicitudId.setEquipo(equipo);

        if (solicitudInscripcionRepository.existsById(solicitudId)) {
            throw new BadRequestException("Ya existe una solicitud de inscripción para este torneo y equipo.");
        }

        SolicitudInscripcion solicitud = new SolicitudInscripcion();
        solicitud.setId(solicitudId);
        solicitud.setEstado(Estado.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        solicitudInscripcionRepository.save(solicitud);
    }

    /**
     * Sortear fase de grupos o liga de un torneo.
     */
     @Transactional
    public void sortearGrupos(Long idTorneo) {
        // Obtener el usuario autenticado
        Usuario user = authService.getAuthenticatedUser();
        if (user == null) throw new UnauthorizedException("Debes estar autenticado.");
        // Verificar si el torneo existe
        Torneo t = torneoRepository.findById(idTorneo)
            .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        // Verificar si el usuario tiene permisos para sortear
        if (!user.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !t.getCreador().getIdUsuario().equals(user.getIdUsuario()))
            throw new AccessDeniedException("No tienes permisos.");
        // Verificar si el torneo tiene fase de grupos o liga
        if (!t.isLiga() && !t.isGrupos())
            throw new BadRequestException("Este torneo no tiene fase de grupos/liguilla.");

            
        List<TorneoEquipos> eq = torneoEquiposService.getAllEquiposByTorneoAndNotEliminados(idTorneo);
        if (t.isLiga()){
            partidoService.crearLiga(t, eq, t.isIdaYVuelta());
        }else{
            partidoService.crearGrupos(t, eq, t.isIdaYVuelta());
        }
    }

    /**
     * Sortear fase eliminatoria de un torneo.
     */
    @Transactional
    public void sortearEliminatoria(Long idTorneo) {
        // Obtener el usuario autenticado
        Usuario user = authService.getAuthenticatedUser();
        if (user == null) throw new UnauthorizedException("Debes estar autenticado.");
        // Verificar si el torneo existe
        Torneo t = torneoRepository.findById(idTorneo)
            .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));
        // Verificar si el usuario tiene permisos para sortear
        if (!user.getRol().equals(Usuario.Rol.ADMINISTRADOR)
            && !t.getCreador().getIdUsuario().equals(user.getIdUsuario()))
            throw new AccessDeniedException("No tienes permisos.");
        // Verificar si el torneo tiene fase de eliminatoria
        if (!t.isEliminatoria())
            throw new BadRequestException("Este torneo no es de eliminatoria.");

        
        List<TorneoEquipos> eq = torneoEquiposService.getAllEquiposByTorneoAndNotEliminados(idTorneo);
        partidoService.crearEliminatorias(t, eq);
    }



}