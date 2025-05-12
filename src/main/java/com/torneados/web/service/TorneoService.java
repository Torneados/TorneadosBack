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
    private final AuthService authService;

    public TorneoService(TorneoRepository torneoRepository, AuthService authService) {
        this.torneoRepository = torneoRepository;
        this.authService = authService;
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
        if (torneo.getEnlaceInstagram() != null && !torneo.getEnlaceInstagram().equals("")  && !torneo.getEnlaceInstagram().contains("instagram.com")) {
            throw new BadRequestException("El enlace de Instagram no parece válido.");
        }
        if (torneo.getEnlaceFacebook() != null && !torneo.getEnlaceFacebook().equals("") && !torneo.getEnlaceFacebook().contains("facebook.com")) {
            throw new BadRequestException("El enlace de Facebook no parece válido.");
        }
        if (torneo.getEnlaceTwitter() != null && !torneo.getEnlaceTwitter().equals("") && !torneo.getEnlaceTwitter().contains("twitter.com")) {
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
     * Obtiene una lista con todos los torneos filtrados según el nombre, lugar y deporte.
     *
     * @param filtroNombre Cadena de texto para filtrar los torneos por nombre.
     * @param filtroLugar Cadena de texto para filtrar los torneos por lugar (opcional).
     * @param filtroDeporte Cadena de texto para filtrar los torneos por deporte (opcional).
     * @return La lista de torneos encontrados.
     */
    public List<Torneo> getTorneosFiltrados(String filtroNombre, String filtroLugar, String filtroDeporte) {
        if ((filtroLugar == null || filtroLugar.isEmpty()) && (filtroDeporte == null || filtroDeporte.isEmpty())) {
            // Solo se filtra por nombre
            return torneoRepository.findByNombreContainingIgnoreCase(filtroNombre);
        } else if (filtroLugar != null && !filtroLugar.isEmpty() && (filtroDeporte == null || filtroDeporte.isEmpty())) {
            // Filtrado por nombre y lugar
            return torneoRepository.findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCase(filtroNombre, filtroLugar);
        } else if ((filtroLugar == null || filtroLugar.isEmpty()) && filtroDeporte != null && !filtroDeporte.isEmpty()) {
            // Filtrado por nombre y deporte
            return torneoRepository.findByNombreContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(filtroNombre, filtroDeporte);
        } else {
            // Filtrado por los tres criterios
            return torneoRepository.findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(
                filtroNombre, filtroLugar, filtroDeporte);
        }
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

        Torneo torneoExistente = torneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));

        // Comprobar permisos: Solo ADMIN o el creador del torneo pueden modificarlo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !torneoExistente.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para modificar este torneo");
        }

        // Validar fechas
        if (torneo.getFechaFin() != null && torneo.getFechaComienzo() != null) {
            if (torneo.getFechaFin().isBefore(torneo.getFechaComienzo())) {
                throw new BadRequestException("La fecha de fin no puede ser anterior a la de comienzo.");
            }
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

        // Actualizar los datos del torneo
        torneoExistente.setNombre(torneo.getNombre());
        torneoExistente.setLugar(torneo.getLugar());
        torneoExistente.setFechaComienzo(torneo.getFechaComienzo());
        torneoExistente.setFechaFin(torneo.getFechaFin());
        torneoExistente.setEnlaceInstagram(torneo.getEnlaceInstagram());
        torneoExistente.setEnlaceFacebook(torneo.getEnlaceFacebook());
        torneoExistente.setEnlaceTwitter(torneo.getEnlaceTwitter());

        // Guardar en la base de datos
        return torneoRepository.save(torneoExistente);
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

        // Comprobar permisos: Solo ADMIN o el creador del torneo pueden eliminarlo
        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !torneo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permisos para eliminar este torneo");
        }

        // Eliminar el torneo
        torneoRepository.delete(torneo);
    }

    
    
}
