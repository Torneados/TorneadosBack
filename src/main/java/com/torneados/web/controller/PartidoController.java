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
@RequestMapping("/torneos/{idTorneo}/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    /**
     * Crea un nuevo partido.
     * Endpoint: POST /torneos/{idTorneo}/partidos
     */
    @Operation(summary = "Crear un nuevo partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Partido creado correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Partido> createPartido(@PathVariable Long idTorneo) {
        Partido nuevoPartido = partidoService.createPartido(idTorneo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoPartido.getIdPartido())
                .toUri();
        return ResponseEntity.created(location).body(nuevoPartido);
    }

    /**
     * Obtiene la lista de partidos asociados a un torneo.
     * Endpoint: GET /torneos/{idTorneo}/partidos
     */
    @Operation(summary = "Obtener partidos de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Partidos obtenidos correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Partido>> getPartidosByTorneo(@PathVariable("idTorneo") Long idTorneo) {
        List<Partido> partidos = partidoService.getPartidosByTorneo(idTorneo);
        return ResponseEntity.ok(partidos);
    }

    /**
     * Actualiza la fecha de un partido.
     * Endpoint: PUT /torneos/{idTorneo}/partidos/{idPartido}
     */
    @Operation(summary = "Actualizar estadísticas de un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Estadísticas actualizadas correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para actualizar estadísticas", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido no encontrado", content = @Content)
    })
    @PutMapping("/{idPartido}")
    public ResponseEntity<Void> updatePartido(@PathVariable Long idTorneo, @PathVariable Long idPartido,
            @RequestBody Partido partidoActualizado) {
        partidoService.updatePartido(idPartido, partidoActualizado, idTorneo);
        return ResponseEntity.noContent().build();
    }
    

    /**
     * Elimina un partido.
     * Endpoint: DELETE /torneos/{idTorneo}/partidos/{idPartido}
     */
    @Operation(summary = "Eliminar un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Partido eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para eliminar el partido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido no encontrado", content = @Content)
    })
    @DeleteMapping("/{idPartido}")
    public ResponseEntity<Void> deletePartido(@PathVariable Long idTorneo, @PathVariable Long idPartido) {
        partidoService.deletePartido(idPartido, idTorneo);
        return ResponseEntity.noContent().build();
    }
}
