package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.TorneoEquipos;
import com.torneados.web.service.TorneoEquiposService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/torneos/{idTorneo}/equipos")
public class TorneoEquiposController {

    private final TorneoEquiposService torneoEquiposService;  

    public TorneoEquiposController(TorneoEquiposService torneoEquiposService) {
        this.torneoEquiposService = torneoEquiposService;
    }

    /*
     * Añadir un equipo a un torneo
     */
    @Operation(summary = "Añadir un equipo a un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Equipo añadido al torneo correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content)
    })
    @PostMapping("/{idEquipo}")
    public ResponseEntity<TorneoEquipos> addEquipoToTorneo(
            @PathVariable Long idTorneo,
            @PathVariable Long idEquipo) {

        TorneoEquipos nuevoTorneoEquipos = torneoEquiposService.addEquipoToTorneo(idTorneo, idEquipo);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).body(nuevoTorneoEquipos);
    }


    /*
     * Obtener todos los equipos de un torneo
     */
    @Operation(summary = "Obtener todos los equipos de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Equipos obtenidos correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content),
    })
    @GetMapping
    public ResponseEntity<List<TorneoEquipos>> getAllEquiposByTorneo(@PathVariable Long idTorneo) {
        List<TorneoEquipos> equipos = torneoEquiposService.getAllEquiposByTorneo(idTorneo);
        return ResponseEntity.ok(equipos);
    }

    /*
     * Obtener los datos de un equipo de un torneo
     */
    @Operation(summary = "Obtener los datos de un equipo de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Equipo obtenido correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo o equipo no encontrado", content = @Content)
    })
    @GetMapping("/{idEquipo}")
    public ResponseEntity<TorneoEquipos> getEquipoById(@PathVariable Long idTorneo, @PathVariable Long idEquipo) {
        TorneoEquipos torneoEquipos = torneoEquiposService.getEquipoById(idTorneo, idEquipo);
        return ResponseEntity.ok(torneoEquipos);
    }


    /*
     * Actualiza los datos de un equipo en un torneo
     */
    @Operation(summary = "Actualizar los datos de un equipo en un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Equipo actualizado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo o equipo no encontrado", content = @Content)
    })
    @PutMapping("/{idEquipo}")
    public ResponseEntity<TorneoEquipos> updateEquipoInTorneo(@PathVariable Long idTorneo, @PathVariable Long idEquipo) {
        TorneoEquipos updatedTorneoEquipos = torneoEquiposService.updateEquipoDataInTorneo(idTorneo, idEquipo);
        return ResponseEntity.ok(updatedTorneoEquipos);
    }

    /*
     * Eliminar un equipo de un torneo
     */
    @Operation(summary = "Eliminar un equipo de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Equipo eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo o equipo no encontrado", content = @Content)
    })
    @DeleteMapping("/{idEquipo}")
    public ResponseEntity<Void> deleteEquipoFromTorneo(@PathVariable Long idTorneo, @PathVariable Long idEquipo) {
        torneoEquiposService.deleteEquipoFromTorneo(idTorneo, idEquipo);
        return ResponseEntity.noContent().build();
    }
    
}
