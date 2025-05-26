package com.torneados.web.controller;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.Equipo;
import com.torneados.web.service.EquipoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @Operation(summary = "Crear un nuevo equipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Equipo creado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Equipo> createEquipo(
            @RequestPart("equipo") @Valid @ModelAttribute Equipo equipo,
            @RequestPart(value = "logo", required = false) MultipartFile logo) throws IOException {

        Equipo nuevoEquipo = equipoService.createEquipo(equipo, logo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(nuevoEquipo.getIdEquipo())
            .toUri();

        return ResponseEntity.created(location).body(nuevoEquipo);
    }

    @Operation(summary = "Obtener un equipo por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Equipo encontrado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Equipo no encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para ver el equipo", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> getEquipoById(@PathVariable Long id) {
        Equipo equipo = equipoService.getEquipoById(id);
        return ResponseEntity.ok(equipo);
    }

    @Operation(summary = "Actualizar un equipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Equipo actualizado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para actualizar el equipo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Equipo no encontrado", content = @Content)
    })
    @PutMapping(value = "/{id_equipo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Equipo> updateEquipo(
            @PathVariable("id_equipo") Long idEquipo,
            @RequestPart("nombre") String nombre,
            @RequestPart(value = "logo", required = false) MultipartFile logo) throws IOException {

        Equipo equipoActualizado = equipoService.updateEquipo(idEquipo, nombre, logo);
        return ResponseEntity.ok(equipoActualizado);
    }

    @DeleteMapping("/{id_equipo}")
    public ResponseEntity<Void> deleteEquipo(@PathVariable("id_equipo") Long idEquipo) {
        equipoService.deleteEquipo(idEquipo);
        return ResponseEntity.noContent().build();
    }
}
