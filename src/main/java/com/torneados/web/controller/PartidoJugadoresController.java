package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.PartidoJugadores;
import com.torneados.web.service.PartidoJugadoresService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/partidos/{idPartido}/jugadores")
public class PartidoJugadoresController {
    
    private final PartidoJugadoresService partidoJugadoresService;

    public PartidoJugadoresController(PartidoJugadoresService partidoJugadoresService) {
        this.partidoJugadoresService = partidoJugadoresService;
    }   

    /*
     * Crear las estadisticas de un jugador en un partido
     */
    @Operation(summary = "Crear las estadisticas de un jugador en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Estadisticas creadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido o jugador no encontrado")
    })
    @PostMapping("/{idJugador}")
    public ResponseEntity<PartidoJugadores> createPartidoJugadores(@PathVariable Long idPartido, @PathVariable Long idJugador) {
        PartidoJugadores nuevoPartidoJugadores = partidoJugadoresService.createPartidoJugadores(idPartido, idJugador);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoPartidoJugadores.getId())
                .toUri();
        return ResponseEntity.created(location).body(nuevoPartidoJugadores);
    }

    /*
     * Obtener las estadisticas de los jugadores en un partido
     */
    @Operation(summary = "Obtener las estadisticas de los jugadores en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Estadisticas obtenidas correctamente"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido no encontrado")
    })
    @GetMapping
    public ResponseEntity<List<PartidoJugadores>> getPartidoJugadores(@PathVariable Long idPartido) {
        List<PartidoJugadores> partidoJugadores = partidoJugadoresService.getPartidoJugadores(idPartido);
        return ResponseEntity.ok(partidoJugadores);
    }
      
    /*
     * Actualizar las estadisticas de un jugador en un partido
     */
    @Operation(summary = "Actualizar las estadisticas de un jugador en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Estadisticas actualizadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido o jugador no encontrado")
    })
    @PutMapping("/{idJugador}")
    public ResponseEntity<PartidoJugadores> updatePartidoJugadores(@PathVariable Long idPartido, @PathVariable Long idJugador, @RequestParam PartidoJugadores partidoJugadores) {
        PartidoJugadores updatedPartidoJugadores = partidoJugadoresService.updatePartidoJugadores(idPartido, idJugador, partidoJugadores);
        return ResponseEntity.ok(updatedPartidoJugadores);
    }

    /*
     * Eliminar las estadisticas de un jugador en un partido
     */
    @Operation(summary = "Eliminar las estadisticas de un jugador en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Estadisticas eliminadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido o jugador no encontrado")
    })
    @DeleteMapping("/{idJugador}")
    public ResponseEntity<Void> deletePartidoJugadores(@PathVariable Long idPartido, @PathVariable Long idJugador) {
        partidoJugadoresService.deletePartidoJugadores(idPartido, idJugador);
        return ResponseEntity.noContent().build();
    }
    
}
