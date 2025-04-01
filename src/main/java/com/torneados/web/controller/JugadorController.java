package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.Jugador;
import com.torneados.web.service.JugadorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    /**
     * Crea un nuevo jugador.
     * Endpoint: POST /jugadores
     */
    @Operation(summary = "Crear un nuevo jugador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Jugador creado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para crear el jugador", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Jugador> createJugador(@RequestBody Jugador jugador) {
        Jugador nuevoJugador = jugadorService.createJugador(jugador);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoJugador.getIdJugador())
                .toUri();
        return ResponseEntity.created(location).body(nuevoJugador);
    }

    /**
     * Obtiene la lista de todos los jugadores.
     * Endpoint: GET /jugadores
     */
    @Operation(summary = "Obtener todos los jugadores")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Jugadores obtenidos correctamente", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Jugador>> getAllJugadores() {
        List<Jugador> jugadores = jugadorService.getAllJugadores();
        return ResponseEntity.ok(jugadores);
    }

    /**
     * Obtiene un jugador por su ID.
     * Endpoint: GET /jugadores/{id}
     */
    @Operation(summary = "Obtener un jugador por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Jugador obtenido correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Jugador no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Jugador> getJugadorById(@PathVariable Long id) {
        Jugador jugador = jugadorService.getJugadorById(id);
        return ResponseEntity.ok(jugador);
    }

    /**
     * Actualiza los datos de un jugador.
     * Endpoint: PUT /jugadores/{id}
     */
    @Operation(summary = "Actualizar un jugador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Jugador actualizado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para actualizar el jugador", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Jugador no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Jugador> updateJugador(@PathVariable Long id, @RequestBody Jugador jugadorActualizado) {
        Jugador jugador = jugadorService.updateJugador(id, jugadorActualizado);
        return ResponseEntity.ok(jugador);
    }

    /**
     * Elimina un jugador.
     * Endpoint: DELETE /jugadores/{id}
     */
    @Operation(summary = "Eliminar un jugador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Jugador eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para eliminar el jugador", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Jugador no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJugador(@PathVariable Long id) {
        jugadorService.deleteJugador(id);
        return ResponseEntity.noContent().build();
    }
}
