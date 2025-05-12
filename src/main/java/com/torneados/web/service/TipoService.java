package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.torneados.web.entities.Tipo;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.repositories.TipoRepository;

@Service
public class TipoService {
    
    private final TipoRepository tipoRepository;
    private final AuthService authService;

    public TipoService(TipoRepository tipoRepository, AuthService authService) {
        this.tipoRepository = tipoRepository;
        this.authService = authService;
    }

    /**
     * Crea un nuevo tipo, solo el administrador puede crear tipos.
     * 
     * @param tipo El tipo a crear.
     * 
     * @return El tipo creado.
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws BadRequestException Si los datos del tipo son inválidos o si el tipo ya existe.
     * @throws AccessDeniedException Si el usuario no tiene permisos para crear tipos.
     */
    public Tipo createTipo(Tipo tipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para crear un tipo.");
        }

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            throw new AccessDeniedException("No tienes permisos para crear tipos.");
        }

        if (tipo.getTipo() == null || tipo.getTipo().isEmpty()) {
            throw new BadRequestException("El nombre del tipo no puede estar vacío.");
        }

        if(tipoRepository.existsByTipo(tipo.getTipo())) {
            throw new BadRequestException("El tipo ya existe.");
        }

        // Guardar en la base de datos
        return tipoRepository.save(tipo);
    }

    /**
     * Obtiene una lista con todos los tipos.
     * 
     * @return Lista de tipos.
     */
    public List<Tipo> getAllTipos() {
        return tipoRepository.findAll();
    }
    
    /**
     * Actualiza un tipo existente.
     * 
     * @param idTipo El ID del tipo a actualizar.
     * @param tipo El nuevo tipo.
     * 
     * @return El tipo actualizado.
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws BadRequestException Si los datos del tipo son inválidos.
     * @throws AccessDeniedException Si el usuario no tiene permisos para actualizar tipos.
     * @throws ResourceNotFoundException Si el tipo no existe.
     */
    public Tipo updateTipo(Long idTipo, Tipo tipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para actualizar un tipo.");
        }

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            throw new AccessDeniedException("No tienes permisos para actualizar tipos.");
        }

        if (tipo.getTipo() == null || tipo.getTipo().isEmpty()) {
            throw new BadRequestException("El nombre del tipo no puede estar vacío.");
        }

        if(tipoRepository.existsByTipo(tipo.getTipo())) {
            throw new BadRequestException("El tipo ya existe.");
        }

        // Buscar el tipo por ID
        Tipo existingTipo = tipoRepository.findById(idTipo).orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado."));
        
        // Actualizar los campos del tipo existente
        existingTipo.setTipo(tipo.getTipo());

        // Guardar en la base de datos
        return tipoRepository.save(existingTipo);
    }

    /**
     * Elimina un tipo existente.
     * 
     * @param idTipo El ID del tipo a eliminar.
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws AccessDeniedException Si el usuario no tiene permisos para eliminar tipos.
     * @throws ResourceNotFoundException Si el tipo no existe.
     */
    public void deleteTipo(Long idTipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para eliminar un tipo.");
        }

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            throw new AccessDeniedException("No tienes permisos para eliminar tipos.");
        }

        // Buscar el tipo por ID
        Tipo existingTipo = tipoRepository.findById(idTipo).orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado."));
        
        // Eliminar el tipo de la base de datos
        tipoRepository.delete(existingTipo);
    }


}
