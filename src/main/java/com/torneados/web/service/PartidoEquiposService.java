package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Partido;
import com.torneados.web.entities.PartidoEquipos;
import com.torneados.web.entities.Usuario;
import com.torneados.web.entities.ids.PartidoEquiposId;
import com.torneados.web.exceptions.*;
import com.torneados.web.repositories.EquipoRepository;
import com.torneados.web.repositories.PartidoEquiposRepository;
import com.torneados.web.repositories.PartidoRepository;

@Service
public class PartidoEquiposService {
    
    private final PartidoEquiposRepository partidoEquiposRepository;
    private final PartidoRepository partidoRepository;
    private final EquipoRepository equipoRepository;
    private final AuthService authService;

    public PartidoEquiposService(PartidoEquiposRepository partidoEquiposRepository, PartidoRepository partidoRepository, EquipoRepository equipoRepository, AuthService authService) {
        this.partidoEquiposRepository = partidoEquiposRepository;
        this.partidoRepository = partidoRepository;
        this.equipoRepository = equipoRepository;
        this.authService = authService;
    }

    /**
     * Crea las estadisticas de un equipo en un partido
     * 
     * @param idPartido ID del partido
     * @param idEquipo ID del equipo
     * 
     * 
     * @return PartidoEquipos creado
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado
     * @throws BadRequestException Si los datos del partido son inválidos
     * @throws ResourceNotFoundException Si el partido o el equipo no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear el partido
     * 
     */
    public PartidoEquipos createPartidoEquipos(Long idPartido, Long idEquipo, int numSet, boolean esLocal) {
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

        // Crear la relación entre el partido y el equipo
        PartidoEquiposId partidoEquiposId = new PartidoEquiposId();
        partidoEquiposId.setPartido(partido);
        partidoEquiposId.setEquipo(equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado.")));
        partidoEquiposId.setNumSet(numSet);
        PartidoEquipos partidoEquipos = new PartidoEquipos();
        partidoEquipos.setId(partidoEquiposId);
        partidoEquipos.setPuntos(0);
        partidoEquipos.setEsLocal(esLocal);
        return partidoEquiposRepository.save(partidoEquipos);
    }

    /**
     * Obtiene las estadisticas de los equipos de un partido
     * 
     * @param idPartido ID del partido
     * @param idEquipo ID del equipo
     * 
     * @return PartidoEquipos encontrado
     * 
     * @throws ResourceNotFoundException Si el partido o el equipo no existen
     */
    public List<PartidoEquipos> getPartidoEquipos(Long idPartido) {
        partidoRepository.findById(idPartido)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado."));
        List<PartidoEquipos> listaEquipos = partidoEquiposRepository.findByIdPartidoIdPartido(idPartido);
        if (listaEquipos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron estadisticas para el partido.");
        }
        return listaEquipos;
    }

    /**
     * Actualiza las estadisticas de un equipo en un partido
     * 
     * @param idPartido ID del partido
     * @param idEquipo ID del equipo
     * 
     * @return PartidoEquipos actualizado
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado
     * @throws BadRequestException Si los datos del partido son inválidos
     * @throws ResourceNotFoundException Si el partido o el equipo no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear el partido
     * 
     */
    public PartidoEquipos updatePartidoEquipos(
    Long idPartido,
    Long idEquipo,
    Integer numSet,
    PartidoEquipos partidoEquiposActualizado
    ) {
        // … autenticación y validaciones como antes …

        Partido partido = partidoRepository.findById(idPartido)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado."));
        Equipo equipo = equipoRepository.findById(idEquipo)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado."));

        // Construyo el PK con el numSet de la ruta
        PartidoEquiposId pk = new PartidoEquiposId();
        pk.setPartido(partido);
        pk.setEquipo(equipo);
        pk.setNumSet(numSet);

        // Cargo (o puedo crear si quisiera)
        PartidoEquipos existing = partidoEquiposRepository.findById(pk)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Estadísticas no encontradas para el set " + numSet));

        // Actualizo sólo lo que interesa (puntos, jugado, tarjetas…)
        existing.setPuntos(partidoEquiposActualizado.getPuntos());
        // si tienes más campos: existing.setJugado(…); etc.

        return partidoEquiposRepository.save(existing);
    }


    /**
     * Elimina las estadisticas de un equipo en un partido
     * 
     * @param idPartido ID del partido
     * @param idEquipo ID del equipo
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado
     * @throws BadRequestException Si los datos del partido son inválidos
     * @throws ResourceNotFoundException Si el partido o el equipo no existen
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear el partido
     * 
     */
    public void deletePartidoEquipos(Long idPartido, Long idEquipo) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        // Validar que el partido existe y que el usuario tiene permiso para borrar las estadisticas del partido
        Partido partido = partidoRepository.findById(idPartido)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado."));
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR) 
            && !partido.getTorneo().getCreador().equals(currentUser)) {
            throw new AccessDeniedException("No tienes permiso para borrar estadisticas de este partido.");
        }

        // Obtener la relación entre el partido y el equipo
        PartidoEquiposId partidoEquiposId = new PartidoEquiposId();
        partidoEquiposId.setPartido(partido);
        partidoEquiposId.setEquipo(equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado.")));
        
        // Eliminar las estadisticas del equipo en el partido
        partidoEquiposRepository.deleteById(partidoEquiposId);
    }
    

}
