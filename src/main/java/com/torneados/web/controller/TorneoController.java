package com.torneados.web.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.*;
import com.torneados.web.entities.SolicitudInscripcion.Estado;
import com.torneados.web.entities.ids.SolicitudInscripcionId;
import com.torneados.web.exceptions.BadRequestException;
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

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

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

    @Operation(summary = "Obtener torneos paginados y filtrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Torneos obtenidos correctamente", content = @Content)
    })
    @GetMapping
    public Page<Torneo> getAllTorneos(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "lugar", required = false) String lugar,
            @RequestParam(name = "deporte", required = false) String deporte,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        boolean hayFiltros = (nombre != null && !nombre.isEmpty())
                        || (lugar != null && !lugar.isEmpty())
                        || (deporte != null && !deporte.isEmpty());

        if (!hayFiltros) {
            // Sin filtros: devolvemos todos los torneos paginados
            return torneoService.getAllTorneos(pageable);
        } else {
            // Con filtros: normalizamos posibles nulos a cadena vacía
            String nom = (nombre != null ? nombre : "");
            String lug = (lugar   != null ? lugar   : "");
            String dep = (deporte != null ? deporte : "");
            return torneoService.getTorneosFiltrados(nom, lug, dep, pageable);
        }
    }

    @Operation(summary = "Obtener un torneo por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Torneo obtenido correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public Torneo getTorneobyId(@PathVariable Long id) {
        return torneoService.getTorneoById(id);
    }

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
     * Actualiza únicamente la fase de un torneo.
     */
    @Operation(summary = "Actualizar únicamente la fase de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Fase actualizada correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Fase inválida", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/fase")
    public ResponseEntity<Void> actualizarFase(
            @PathVariable("id") Long idTorneo,
            @RequestBody Map<String, Integer> payload) {

        Integer nuevaFase = payload.get("fase");
        if (nuevaFase == null) {
            throw new BadRequestException("Debes enviar un valor de fase en el cuerpo de la petición");
        }

        torneoService.actualizarFase(idTorneo, nuevaFase);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Eliminar un torneo por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Torneo eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: ID inválido", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTorneo(@PathVariable Long id) {
        torneoService.deleteTorneo(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Solicitar inscripción a un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Solicitud registrada", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Ya existe una solicitud", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content)
    })
    @PostMapping("/{idTorneo}/solicitudes")
    public ResponseEntity<Void> solicitarInscripcion(@PathVariable Long idTorneo, @RequestBody Equipo equipo) {
        torneoService.solicitarInscripcion(idTorneo, equipo);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Obtener solicitudes de inscripción de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Solicitudes obtenidas correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @GetMapping("/{idTorneo}/solicitudes")
    public ResponseEntity<List<SolicitudInscripcion>> getSolicitudes(@PathVariable Long idTorneo) {
        List<SolicitudInscripcion> solicitudes = torneoService.getSolicitudesInscripcion(idTorneo);
        return ResponseEntity.ok(solicitudes);
    }

    @Operation(summary = "Cambiar el estado de una solicitud de inscripción")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Estado actualizado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Solicitud no encontrada", content = @Content)
    })
    @PutMapping("/{idTorneo}/solicitudes/{idEquipo}")
    public ResponseEntity<Void> cambiarEstadoSolicitud(
            @PathVariable Long idTorneo,
            @PathVariable Long idEquipo,
            @RequestParam Estado nuevoEstado) {

        Torneo torneo = torneoService.getTorneoById(idTorneo);
        Equipo equipo = new Equipo();
        equipo.setIdEquipo(idEquipo);

        SolicitudInscripcionId idSolicitud = new SolicitudInscripcionId();
        idSolicitud.setTorneo(torneo);
        idSolicitud.setEquipo(equipo);

        torneoService.cambiarEstadoSolicitudInscripcion(idSolicitud, nuevoEstado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Sortear fase de grupos de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Grupos sorteados correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: No se puede sortear la fase de grupos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @PostMapping("/{idTorneo}/sortear/grupos")
    public ResponseEntity<Void> sortearGrupos(@PathVariable Long idTorneo) {
        torneoService.sortearGrupos(idTorneo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Sortear fase eliminatoria de un torneo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Eliminatoria sorteada correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: No se puede sortear la eliminatoria", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Torneo no encontrado", content = @Content)
    })
    @PostMapping("/{idTorneo}/sortear/eliminatoria")
    public ResponseEntity<Void> sortearEliminatoria(@PathVariable Long idTorneo) {
        torneoService.sortearEliminatoria(idTorneo);
        return ResponseEntity.noContent().build();
    }

}
