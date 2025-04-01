package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.Partido;
import com.torneados.web.service.PartidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    /**
     * Crea un nuevo partido.
     * Endpoint: POST /partidos
     */
    @Operation(summary = "Crear un nuevo partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Partido creado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para crear el partido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Partido> createPartido(@RequestBody Partido partido) {
        Partido nuevoPartido = partidoService.createPartido(partido);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(nuevoPartido.getIdPartido())
            .toUri();
        return ResponseEntity.created(location).body(nuevoPartido);
    }

    /**
     * Obtiene los detalles de un partido por su ID.
     * Endpoint: GET /partidos/{id}
     */
    @Operation(summary = "Obtener un partido por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Partido obtenido correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Partido> getPartidoById(@PathVariable Long id) {
        Partido partido = partidoService.getPartidoById(id);
        return ResponseEntity.ok(partido);
    }

    /**
     * Obtiene la lista de partidos asociados a un torneo.
     * Endpoint: GET /partidos/torneo/{idTorneo}
     */
    @Operation(summary = "Obtener partidos de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Partidos obtenidos correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @GetMapping("/torneo/{idTorneo}")
    public ResponseEntity<List<Partido>> getPartidosByTorneo(@PathVariable("idTorneo") Long idTorneo) {
        List<Partido> partidos = partidoService.getPartidosByTorneo(idTorneo);
        return ResponseEntity.ok(partidos);
    }

    /**
     * Actualiza las estadísticas de un partido.
     * Endpoint: PUT /partidos/{id}/estadisticas
     */
    @Operation(summary = "Actualizar estadísticas de un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Estadísticas actualizadas correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para actualizar estadísticas", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido no encontrado", content = @Content)
    })
    @PutMapping("/{id}/estadisticas")
    public ResponseEntity<Void> updatePartidoEstadisticas(@PathVariable Long id, @RequestBody Partido partidoActualizado) {
        partidoService.updatePartidoEstadisticas(id, partidoActualizado);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un partido.
     * Endpoint: DELETE /partidos/{id}
     */
    @Operation(summary = "Eliminar un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Partido eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para eliminar el partido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartido(@PathVariable Long id) {
        partidoService.deletePartido(id);
        return ResponseEntity.noContent().build();
    }
}
