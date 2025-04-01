package com.torneados.web.service;

import java.util.List; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import com.torneados.web.entities.Partido; import com.torneados.web.entities.Torneo; import com.torneados.web.entities.Usuario; import com.torneados.web.exceptions.BadRequestException; import com.torneados.web.exceptions.ResourceNotFoundException; import com.torneados.web.exceptions.AccessDeniedException; import com.torneados.web.repositories.PartidoRepository; import com.torneados.web.repositories.TorneoRepository;

@Service public class PartidoService {
    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;
    private final AuthService authService;

    public PartidoService(PartidoRepository partidoRepository, TorneoRepository torneoRepository, AuthService authService) {
        this.partidoRepository = partidoRepository;
        this.torneoRepository = torneoRepository;
        this.authService = authService;
    }

    /**
     * Crea un nuevo partido.
     * Requisitos:
     * - El partido debe estar asociado a un torneo existente.
     * - Se debe proporcionar la fecha de comienzo.
     * - Solo el creador del torneo o un ADMINISTRADOR pueden crear el partido.
     *
     * @param partido Objeto con los datos del partido a crear.
     * @return El partido creado.
     */
    public Partido createPartido(Partido partido) {
        // Validar que el partido tenga un torneo asociado
        if (partido.getTorneo() == null || partido.getTorneo().getIdTorneo() == null) {
            throw new BadRequestException("El partido debe estar asociado a un torneo.");
        }
        
        // Validar que el torneo exista
        Torneo torneo = torneoRepository.findById(partido.getTorneo().getIdTorneo())
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con id: " + partido.getTorneo().getIdTorneo()));
        partido.setTorneo(torneo);
        
        // Validar que se proporcione la fecha de comienzo
        if (partido.getFechaComienzo() == null) {
            throw new BadRequestException("La fecha de comienzo del partido es obligatoria.");
        }
        
        // Obtener el usuario autenticado
        Usuario currentUser = authService.getAuthenticatedUser();
        
        // Verificar permisos: solo ADMIN o el creador del torneo pueden crear el partido
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) &&
            (torneo.getCreador() == null || !torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario()))) {
            throw new AccessDeniedException("No tienes permisos para crear un partido en este torneo.");
        }
        
        // Guardar el partido
        return partidoRepository.save(partido);
    }

    /**
     * Obtiene los detalles de un partido por su ID.
     *
     * @param id ID del partido.
     * @return El partido encontrado.
     * @throws ResourceNotFoundException Si el partido no existe.
     */
    public Partido getPartidoById(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con id: " + idTorneo));
        
        return partidoRepository.findByTorneoIdTorneo(torneo.getIdTorneo());
    }

    /**
     * Actualiza las estadísticas de un partido.
     * Nota: La implementación dependerá de los campos de estadísticas que se quieran actualizar.
     * Por ejemplo, si se agregan campos como 'puntos' o 'juegos', se deben actualizar aquí.
     *
     * @param id ID del partido a actualizar.
     * @param partidoActualizado Objeto con las estadísticas actualizadas.
     * @throws ResourceNotFoundException Si el partido no existe.
     * @throws AccessDeniedException Si el usuario no tiene permisos para modificar.
     */
    @Transactional
    public void updatePartidoEstadisticas(Long id, Partido partidoActualizado) {
        Partido partido = getPartidoById(id);
        Usuario currentUser = authService.getAuthenticatedUser();
        
        // Verificar permisos: solo ADMIN o el creador del torneo pueden actualizar estadísticas
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) &&
            (partido.getTorneo().getCreador() == null || !partido.getTorneo().getCreador().getIdUsuario().equals(currentUser.getIdUsuario()))) {
            throw new AccessDeniedException("No tienes permisos para modificar las estadísticas de este partido.");
        }
        
        // Aquí se actualizarían los campos de estadísticas.
        // Por ejemplo:
        // partido.setPuntos(partidoActualizado.getPuntos());
        // partido.setJuegos(partidoActualizado.getJuegos());
        // Como el modelo actual de Partido no incluye estadísticas adicionales,
        // esta parte se deja como implementación pendiente según la evolución del modelo.
        
        partidoRepository.save(partido);
    }

    /**
     * Elimina un partido.
     *
     * @param id ID del partido a eliminar.
     * @throws ResourceNotFoundException Si el partido no existe.
     * @throws AccessDeniedException Si el usuario no tiene permisos para eliminarlo.
     */
    public void deletePartido(Long id) {
        Partido partido = getPartidoById(id);
        Usuario currentUser = authService.getAuthenticatedUser();
        
        // Verificar permisos: solo ADMIN o el creador del torneo pueden eliminar el partido
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) &&
            (partido.getTorneo().getCreador() == null || !partido.getTorneo().getCreador().getIdUsuario().equals(currentUser.getIdUsuario()))) {
            throw new AccessDeniedException("No tienes permisos para eliminar este partido.");
        }
        
        partidoRepository.delete(partido);
    }
}

