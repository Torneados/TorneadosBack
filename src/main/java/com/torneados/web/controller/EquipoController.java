package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Jugador;
import com.torneados.web.service.EquipoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    /**
     * Crear un nuevo equipo (POST /equipos)
     */
    @Operation(summary = "Crear un nuevo equipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Equipo creado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para crear el equipo", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Equipo> createEquipo(@RequestBody Equipo equipo) {
        Equipo nuevoEquipo = equipoService.createEquipo(equipo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoEquipo.getIdEquipo())
                .toUri();
        return ResponseEntity.created(location).body(nuevoEquipo);
    }

    /**
     * Obtener la lista de equipos de un usuario (GET /equipos/usuarios/{id_usuario})
     * Opcionalmente se puede aplicar un filtro a través del parámetro "filtro".
     */
    @Operation(summary = "Obtener equipos de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Equipos obtenidos correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Usuario no encontrado", content = @Content)
    })
    @GetMapping("/usuarios/{id_usuario}")
    public ResponseEntity<List<Equipo>> getEquiposByUsuario(
            @PathVariable("id_usuario") Long idUsuario,
            @RequestParam(value = "filtro", required = false) String filtro) {
        List<Equipo> equipos;
        if (filtro != null && !filtro.trim().isEmpty()) {
            equipos = equipoService.getEquiposByUsuarioWithFilter(idUsuario, filtro);
        } else {
            equipos = equipoService.getEquiposByUsuario(idUsuario);
        }
        return ResponseEntity.ok(equipos);
    }

    /**
     * Obtener la lista de jugadores que pertenecen a un equipo (GET /equipos/{id_equipo}/jugadores)
     */
    @Operation(summary = "Obtener jugadores de un equipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Jugadores obtenidos correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Equipo no encontrado", content = @Content)
    })
    @GetMapping("/{id_equipo}/jugadores")
    public ResponseEntity<List<Jugador>> getJugadoresByEquipo(@PathVariable("id_equipo") Long idEquipo) {
        List<Jugador> jugadores = equipoService.getJugadoresByEquipo(idEquipo);
        return ResponseEntity.ok(jugadores);
    }

    /**
     * Añadir un jugador a un equipo (POST /equipos/{id_equipo}/jugadores)
     */
    @Operation(summary = "Añadir un jugador a un equipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Jugador añadido correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Equipo no encontrado", content = @Content)
    })
    @PostMapping("/{id_equipo}/jugadores")
    public ResponseEntity<Jugador> addJugadorToEquipo(@PathVariable("id_equipo") Long idEquipo,
                                                       @RequestBody Jugador jugador) {
        Jugador nuevoJugador = equipoService.addJugadorToEquipo(idEquipo, jugador);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoJugador.getIdJugador())
                .toUri();
        return ResponseEntity.created(location).body(nuevoJugador);
    }

    /**
     * Eliminar un equipo (DELETE /equipos/{id_equipo})
     */
    @Operation(summary = "Eliminar un equipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Equipo eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para eliminar el equipo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Equipo no encontrado", content = @Content)
    })
    @DeleteMapping("/{id_equipo}")
    public ResponseEntity<Void> deleteEquipo(@PathVariable("id_equipo") Long idEquipo) {
        equipoService.deleteEquipo(idEquipo);
        return ResponseEntity.noContent().build();
    }
}
