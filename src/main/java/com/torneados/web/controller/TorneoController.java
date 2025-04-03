package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoTorneo.getIdTorneo())
                .toUri();
        return ResponseEntity.created(location).body(nuevoTorneo);
    }



    /**
     * Obtener todos los torneos (GET /torneos)
     */
    @Operation(summary = "Obtener todos los torneos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Torneos obtenidos correctamente", content = @Content)
    })
    @GetMapping
    public List<Torneo> getAllTorneos(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "lugar", required = false) String lugar,
            @RequestParam(name = "deporte", required = false) String deporte) {

        // Si no se proporciona ningún filtro, se devuelven todos los torneos.
        if ((nombre == null || nombre.isEmpty())
                && (lugar == null || lugar.isEmpty())
                && (deporte == null || deporte.isEmpty())) {
            return torneoService.getAllTorneos();
        }
        // Si se proporcionan filtros, se delega en el método que aplica los filtros.
        return torneoService.getTorneosFiltrados(
                nombre != null ? nombre : "",
                lugar != null ? lugar : "",
                deporte != null ? deporte : ""
        );
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
     * Actualizar un torneo por ID (PUT /torneos/{id})
     */
    @Operation(summary = "Actualizar un torneo por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Torneo actualizado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Torneo> updateTorneo(@PathVariable Long id, @Valid @RequestBody Torneo torneo) {
        Torneo torneoActualizado = torneoService.updateTorneo(id, torneo);
        return ResponseEntity.ok(torneoActualizado);
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
