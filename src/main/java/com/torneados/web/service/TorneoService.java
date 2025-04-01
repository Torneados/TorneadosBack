package com.torneados.web.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.repositories.TorneoRepository;

@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final AuthService authService; // Obtener el usuario autenticado

    public TorneoService(TorneoRepository torneoRepository, AuthService authService) {
        this.torneoRepository = torneoRepository;
        this.authService = authService;
    }

    public Torneo createTorneo(Torneo torneo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para crear un torneo.");
        }

        if (torneo.getFechaFin().isBefore(torneo.getFechaComienzo())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la de comienzo.");
        }
        if (torneo.getEnlaceInstagram() != null && !torneo.getEnlaceInstagram().contains("instagram.com")) {
            throw new BadRequestException("El enlace de Instagram no parece válido.");
        }
        if (torneo.getEnlaceFacebook() != null && !torneo.getEnlaceFacebook().contains("facebook.com")) {
            throw new BadRequestException("El enlace de Facebook no parece válido.");
        }
        if (torneo.getEnlaceTwitter() != null && !torneo.getEnlaceTwitter().contains("twitter.com")) {
            throw new BadRequestException("El enlace de Twitter no parece válido.");
        }

        // Asignar el usuario autenticado como creador del torneo
        torneo.setCreador(currentUser);

        // Guardar en la base de datos
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
     * Obtiene los datos de un torneo por su ID.
     *
     * @param id ID del torneo a buscar.
     * @return El torneo encontrado.
     * @throws ResourceNotFoundException Si el torneo no existe.
     */
    public Torneo getTorneoById(Long id) {
        return torneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));
    }

    /**
     * Borra un torneo, validando si el usuario autenticado tiene permisos para eliminarlo.
     *
     * @param id ID del torneo a eliminar.
     * @throws AccessDeniedException Si el usuario no tiene permisos para eliminar.
     * @throws ResourceNotFoundException Si el torneo no existe.
     */
    public void deleteTorneo(Long id) {
        // Obtener usuario autenticado
        Usuario currentUser = authService.getAuthenticatedUser(); // Usuario autenticado vía JWT

        // Verificar si el torneo existe
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + id));

        // Validar que el torneo tiene un creador antes de acceder a su ID
        if (torneo.getCreador() == null) {
            throw new AccessDeniedException("El torneo no tiene un creador asignado, no puede ser eliminado.");
        }

        // Comprobar permisos: Solo ADMIN o el creador del torneo pueden eliminarlo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para eliminar este torneo");
        }

        // Eliminar el torneo
        torneoRepository.delete(torneo);
    }
}
