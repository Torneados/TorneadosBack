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
@RequestMapping("/equipos/{idEquipo}/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    /**
     * Crea un nuevo jugador.
     * Endpoint: POST /equipos/{idEquipo}/jugadores
     */
    @Operation(summary = "Crear un nuevo jugador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Jugador creado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Equipo no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Jugador> createJugador(@PathVariable Long idEquipo, @RequestBody Jugador jugador) {
        Jugador nuevoJugador = jugadorService.createJugador(jugador, idEquipo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoJugador.getIdJugador())
                .toUri();
        return ResponseEntity.created(location).body(nuevoJugador);
    }

    /**
     * Obtiene todos los jugadores de un equipo.
     * Endpoint: GET /equipos/{idEquipo}/jugadores
     */
    @Operation(summary = "Obtener todos los jugadores de un equipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Lista de jugadores obtenida correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Equipo no encontrado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Jugador>> getJugadores(@PathVariable Long idEquipo) {
        List<Jugador> jugadores = jugadorService.getJugadoresByEquipo(idEquipo);
        return ResponseEntity.ok(jugadores);
    }

    /**
     * Actualiza un jugador existente.
     * Endpoint: PUT /equipos/{idEquipo}/jugadores/{idJugador}
     */
    @Operation(summary = "Actualizar un jugador existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Jugador actualizado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Jugador o equipo no encontrado", content = @Content)
    })
    @PutMapping("/{idJugador}")
    public ResponseEntity<Jugador> updateJugador(@PathVariable Long idEquipo, @PathVariable Long idJugador, @RequestBody Jugador jugador) {
        Jugador jugadorActualizado = jugadorService.updateJugador(idJugador, jugador, idEquipo);
        return ResponseEntity.ok(jugadorActualizado);
    }

    /**
     * Elimina un jugador existente.
     * Endpoint: DELETE /equipos/{idEquipo}/jugadores/{idJugador}
     */
    @Operation(summary = "Eliminar un jugador existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Jugador eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Jugador o equipo no encontrado", content = @Content)
    })
    @DeleteMapping("/{idJugador}")
    public ResponseEntity<Void> deleteJugador(@PathVariable Long idEquipo, @PathVariable Long idJugador) {
        jugadorService.deleteJugador(idJugador, idEquipo);
        return ResponseEntity.noContent().build();
    }
}
