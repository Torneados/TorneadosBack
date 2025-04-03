package com.torneados.web.service;

import java.util.List; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 
import com.torneados.web.entities.Partido; 
import com.torneados.web.entities.Torneo; 
import com.torneados.web.entities.Usuario; 
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.repositories.PartidoRepository; import com.torneados.web.repositories.TorneoRepository;

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
     *
     * @param partido Objeto con los datos del partido a crear.
     * @param idTorneo ID del torneo al que pertenece el partido.
     * @return El partido creado.
     */
    public Partido createPartido(Partido partido, Long idTorneo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }
        
        // Verificar que el torneo exista
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con id: " + idTorneo));
        
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
    
}

