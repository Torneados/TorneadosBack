package com.torneados.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.torneados.web.entities.Torneo;
import com.torneados.web.service.TorneoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/torneos")
public class TorneoController {

    private final TorneoService torneoService;

    // Inyección de dependencias por constructor
    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    /**
     * Crear un nuevo torneo (POST /torneos)
     */
    @Operation(summary = "Crear un nuevo torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Torneo creado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Torneo> createTorneo(@Valid @RequestBody Torneo torneo) {
        Torneo nuevoTorneo = torneoService.createTorneo(torneo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTorneo);
    }


    /**
     * Obtener todos los torneos (GET /torneos)
     */
    @Operation(summary = "Obtener todos los torneos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Torneos obtenidos correctamente", content = @Content)
    })
    @GetMapping
    public List<Torneo> getAllTorneos() {
        return torneoService.getAllTorneos();
    }

    /**
     * Obtener un torneo por id (GET /torneos/{id})
     */
    @Operation(summary = "Obtener un torneo por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Torneo obtenido correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public Torneo getTorneobyId(@PathVariable Long id) {
        return torneoService.getTorneoById(id);
    }

    /**
     * Eliminar un torneo por ID (DELETE /torneos/{id})
     */
    @Operation(summary = "Eliminar un torneo por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Torneo eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: ID inválido", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTorneo(@PathVariable Long id) {
        torneoService.deleteTorneo(id);
        return ResponseEntity.noContent().build();
    }
}
