package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.PartidoEquipos;
import com.torneados.web.service.PartidoEquiposService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/partidos/{idPartido}/equipos")
public class PartidoEquiposController {
    
    private final PartidoEquiposService partidoEquiposService;

    public PartidoEquiposController(PartidoEquiposService partidoEquiposService) {
        this.partidoEquiposService = partidoEquiposService;
    }

    /*
     * Crear las estadisticas de un equipo en un partido 
     */
    @Operation(summary = "Crear las estadisticas de un equipo en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Estadisticas creadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido o equipo no encontrado")
    })
    @PostMapping("/{idEquipo}")
    public ResponseEntity<PartidoEquipos> createPartidoEquipos(@PathVariable Long idPartido, @PathVariable Long idEquipo) {
        PartidoEquipos nuevoPartidoEquipos = partidoEquiposService.createPartidoEquipos(idPartido, idEquipo, 0, false);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoPartidoEquipos.getId())
                .toUri();
        return ResponseEntity.created(location).body(nuevoPartidoEquipos);
    }

    /*
     * Obtener las estadisticas de los equipo en un partido
     */
    @Operation(summary = "Obtener las estadisticas de un equipo en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Estadisticas obtenidas correctamente"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido o equipo no encontrado")
    })
    @GetMapping
    public ResponseEntity<List<PartidoEquipos>> getPartidoEquipos(@PathVariable Long idPartido) {
        List<PartidoEquipos> partidoEquipos = partidoEquiposService.getPartidoEquipos(idPartido);
        return ResponseEntity.ok(partidoEquipos);
    }

    /*
     * Actualizar las estadisticas de un equipo en un partido
     */
    @Operation(summary = "Actualizar las estadisticas de un equipo en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Estadisticas actualizadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido o equipo no encontrado")
    })
    @PutMapping("/{idEquipo}")
    public ResponseEntity<PartidoEquipos> updatePartidoEquipos(@PathVariable Long idPartido, @PathVariable Long idEquipo, @RequestBody PartidoEquipos partidoEquipos) {
        PartidoEquipos updatedPartidoEquipos = partidoEquiposService.updatePartidoEquipos(idPartido, idEquipo, partidoEquipos);
        return ResponseEntity.ok(updatedPartidoEquipos);
    }

    /*
     * Eliminar las estadisticas de un equipo en un partido
     */
    @Operation(summary = "Eliminar las estadisticas de un equipo en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Estadisticas eliminadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o lógicos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Partido o equipo no encontrado")
    })
    @DeleteMapping("/{idEquipo}")
    public ResponseEntity<Void> deletePartidoEquipos(@PathVariable Long idPartido, @PathVariable Long idEquipo) {
        partidoEquiposService.deletePartidoEquipos(idPartido, idEquipo);
        return ResponseEntity.noContent().build();
    }
}
