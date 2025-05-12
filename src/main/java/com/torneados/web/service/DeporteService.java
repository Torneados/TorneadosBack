package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.torneados.web.entities.Deporte;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.repositories.DeporteRepository;

@Service
public class DeporteService {
    
    private final DeporteRepository deporteRepository;
    private final AuthService authService;

    public DeporteService(DeporteRepository deporteRepository, AuthService authService) {
        this.deporteRepository = deporteRepository;
        this.authService = authService;
    }

    /**
     * Crea un nuevo deporte (solo el admin puede)
     * 
     * @param deporte El deporte a crear.
     * 
     * @return El deporte creado.
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws BadRequestException Si los datos del deporte son inválidos o el deporte ya existe.
     * @throws AccessDeniedException Si el usuario no tiene permiso para crear un deporte.
     * 
     */
    public Deporte createDeporte(Deporte deporte) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para crear un deporte.");
        }

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            throw new AccessDeniedException("No tienes permiso para crear un deporte.");
        }

        if (deporte.getDeporte() == null || deporte.getDeporte().isEmpty()) {
            throw new BadRequestException("El nombre del deporte no puede estar vacío.");
        }

        // Verificar si el deporte ya existe
        if (deporteRepository.existsByDeporte(deporte.getDeporte())) {
            throw new BadRequestException("El deporte ya existe.");
        }

        // Guardar en la base de datos
        return deporteRepository.save(deporte);
    }

    /**
     * Obtiene una lista con todos los deportes.
     * 
     * @return Lista de deportes.
     */
    public List<Deporte> getAllDeportes() {
        return deporteRepository.findAll();
    }

    /**
     * Actualiza un deporte (solo el admin puede).
     * 
     * @param id El ID del deporte a actualizar.
     * @param deporte El nuevo deporte.
     * 
     * @return El deporte actualizado.
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws AccessDeniedException Si el usuario no tiene permiso para actualizar un deporte.
     * @throws BadRequestException Si los datos del deporte son inválidos o el ID no es válido.
     * @throws ResourceNotFoundException Si el deporte no existe.
     */
    public Deporte updateDeporte(Long id, Deporte deporte) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para actualizar un deporte.");
        }

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            throw new AccessDeniedException("No tienes permiso para actualizar un deporte.");
        }

        if (id == null || id <= 0) {
            throw new BadRequestException("El ID del deporte no es válido.");
        }

        if (deporte.getDeporte() == null || deporte.getDeporte().isEmpty()) {
            throw new BadRequestException("El nombre del deporte no puede estar vacío.");
        }

        // Verificar si el deporte ya existe
        if (deporteRepository.existsByDeporte(deporte.getDeporte())) {
            throw new BadRequestException("El deporte ya existe.");
        }

        // Buscar el deporte por ID
        Deporte existingDeporte = deporteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Deporte no encontrado."));
        
        // Actualizar los campos del deporte existente
        existingDeporte.setDeporte(deporte.getDeporte());
        
        // Guardar en la base de datos
        return deporteRepository.save(existingDeporte);
    }


    /**
     * Elimina un deporte por su ID (solo el admin puede).
     * 
     * @param id El ID del deporte a eliminar.
     * 
     * @throws UnauthorizedException Si el usuario no está autenticado.
     * @throws AccessDeniedException Si el usuario no tiene permiso para eliminar un deporte.
     * @throws BadRequestException Si el ID del deporte no es válido
     * @throws ResourceNotFoundException Si el deporte no existe.
     * 
     */
    public void deleteDeporte(Long id) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Debes estar autenticado para eliminar un deporte.");
        }

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)) {
            throw new AccessDeniedException("No tienes permiso para eliminar un deporte.");
        }

        if (id == null || id <= 0) {
            throw new BadRequestException("El ID del deporte no es válido.");
        }

        Deporte deporte = deporteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Deporte no encontrado."));
        deporteRepository.delete(deporte);
    }
}
