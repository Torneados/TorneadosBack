package com.torneados.web.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.BadRequestException;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.repositories.JugadorRepository;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final AuthService authService;
    
    public JugadorService(JugadorRepository jugadorRepository, AuthService authService) {
        this.jugadorRepository = jugadorRepository;
        this.authService = authService;
    }
    
    /**
     * Crea un nuevo jugador.
     * 
     * @param jugador Objeto con los datos del jugador a crear.
     * @return El jugador creado.
     * @throws BadRequestException Si los datos del jugador son inválidos.
     * @throws AccessDeniedException Si no hay usuario autenticado.
     */
    public Jugador createJugador(Jugador jugador) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        // Validar datos básicos
        if (jugador.getNombre() == null || jugador.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del jugador es obligatorio.");
        }
        if (jugador.getDni() == null || jugador.getDni().trim().isEmpty()) {
            throw new BadRequestException("El DNI del jugador es obligatorio.");
        }
        // Validación opcional: verificar que el DNI no esté ya registrado
        if (jugadorRepository.existsByDni(jugador.getDni())) {
            throw new BadRequestException("El DNI ya está registrado.");
        }
        
        return jugadorRepository.save(jugador);
    }
    
    /**
     * Obtiene un jugador por su ID.
     * 
     * @param id ID del jugador.
     * @return El jugador encontrado.
     * @throws ResourceNotFoundException Si no se encuentra el jugador.
     */
    public Jugador getJugadorById(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con id: " + id));
    }
    
    /**
     * Obtiene la lista de todos los jugadores.
     * 
     * @return Lista de jugadores.
     */
    public List<Jugador> getAllJugadores() {
        return jugadorRepository.findAll();
    }
    
    /**
     * Actualiza los datos de un jugador.
     * 
     * @param id ID del jugador a actualizar.
     * @param jugadorActualizado Objeto con los datos actualizados.
     * @return El jugador actualizado.
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     * @throws AccessDeniedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si no se encuentra el jugador.
     */
    @Transactional
    public Jugador updateJugador(Long id, Jugador jugadorActualizado) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        Jugador jugador = getJugadorById(id);
        
        // Actualizar campos, validando que no sean nulos o vacíos
        if (jugadorActualizado.getNombre() == null || jugadorActualizado.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del jugador es obligatorio.");
        }
        jugador.setNombre(jugadorActualizado.getNombre());
        
        if (jugadorActualizado.getDni() == null || jugadorActualizado.getDni().trim().isEmpty()) {
            throw new BadRequestException("El DNI del jugador es obligatorio.");
        }
        // Si se cambia el DNI, verificar que no esté duplicado
        if (!jugador.getDni().equals(jugadorActualizado.getDni()) &&
            jugadorRepository.existsByDni(jugadorActualizado.getDni())) {
            throw new BadRequestException("El DNI ya está registrado.");
        }
        jugador.setDni(jugadorActualizado.getDni());
        
        if (jugadorActualizado.getFechaNacimiento() != null) {
            jugador.setFechaNacimiento(jugadorActualizado.getFechaNacimiento());
        }
        
        return jugadorRepository.save(jugador);
    }
    
    /**
     * Elimina un jugador por su ID.
     * 
     * @param id ID del jugador a eliminar.
     * @throws AccessDeniedException Si no hay usuario autenticado.
     * @throws ResourceNotFoundException Si no se encuentra el jugador.
     */
    public void deleteJugador(Long id) {
        // Verificar autenticación
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }
        
        Jugador jugador = getJugadorById(id);
        jugadorRepository.delete(jugador);
    }
}
