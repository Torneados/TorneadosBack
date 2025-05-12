package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.TorneoJugadores;
import com.torneados.web.service.TorneoJugadoresService;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/torneos/{idTorneo}/jugadores")
public class TorneoJugadoresController {
    
    private final TorneoJugadoresService torneoJugadoresService;

    public TorneoJugadoresController(TorneoJugadoresService torneoJugadoresService) {
        this.torneoJugadoresService = torneoJugadoresService;
    }

    /*
     * Crea las estadisticas de un jugador en un torneo
     */
    @PostMapping("/{idJugador}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Estadisticas creadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo o jugador no encontrado")
    })
    public ResponseEntity<TorneoJugadores> createTorneoJugadores(@PathVariable Long idTorneo, @PathVariable Long idJugador) {
        TorneoJugadores nuevoTorneoJugadores = torneoJugadoresService.createTorneoJugadores(idTorneo, idJugador);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoTorneoJugadores.getId())
                .toUri();
        return ResponseEntity.created(location).body(nuevoTorneoJugadores);
    }

    /*
     * Obtener las estadisticas de los jugadores en un torneo
     */
    @GetMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Estadisticas obtenidas correctamente"),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo o jugador no encontrado")
    })
    public ResponseEntity<List<TorneoJugadores>> getTorneoJugadores(@PathVariable Long idTorneo) {
        List<TorneoJugadores> torneoJugadores = torneoJugadoresService.getTorneoJugadores(idTorneo);
        return ResponseEntity.ok(torneoJugadores);
    }

    /*
     * Actualizar las estadisticas de un jugador en un torneo
     */
    @PutMapping("/{idJugador}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Estadisticas actualizadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo o jugador no encontrado")
    })
    public ResponseEntity<TorneoJugadores> updateTorneoJugadores(@PathVariable Long idTorneo, @PathVariable Long idJugador, @RequestBody TorneoJugadores torneoJugadores) {
        TorneoJugadores updatedTorneoJugadores = torneoJugadoresService.updateTorneoJugadores(idTorneo, idJugador, torneoJugadores);
        return ResponseEntity.ok(updatedTorneoJugadores);
    }

    /*
     * Eliminar las estadisticas de un jugador en un torneo
     */
    @DeleteMapping("/{idJugador}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Estadisticas eliminadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo o jugador no encontrado")
    })
    public ResponseEntity<Void> deleteTorneoJugadores(@PathVariable Long idTorneo, @PathVariable Long idJugador) {
        torneoJugadoresService.deleteTorneoJugadores(idTorneo, idJugador);
        return ResponseEntity.noContent().build();
    }
}
