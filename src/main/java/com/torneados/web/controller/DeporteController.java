package com.torneados.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.Deporte;
import com.torneados.web.service.DeporteService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deportes")
public class DeporteController {
    
    private final DeporteService deporteService;

    public DeporteController(DeporteService deporteService) {
        this.deporteService = deporteService;
    }
    
    /*
     * Crea un nuevo deporte (solo el admin puede)
     */
    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Deporte creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o el deporte ya existe"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos")
    })
    public ResponseEntity<Deporte> createDeporte(@RequestBody Deporte deporte) {
        Deporte nuevoDeporte = deporteService.createDeporte(deporte);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoDeporte.getIdDeporte())
                .toUri();
        return ResponseEntity.created(location).body(nuevoDeporte);
    }

    /**
     * Obtener todos los deportes (GET /deportes)
     */
    @GetMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Deportes obtenidos correctamente")
    })
    public ResponseEntity<List<Deporte>> getAllDeportes() {
        List<Deporte> deportes = deporteService.getAllDeportes();
        return ResponseEntity.ok(deportes);
    }

    /**
     * Actualizar un deporte (PUT /deportes/{id})
     */
    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Deporte actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o el deporte ya existe"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Deporte no encontrado")
    })
    public ResponseEntity<Deporte> updateDeporte(@PathVariable Long id, @RequestBody Deporte deporte) {
        Deporte updatedDeporte = deporteService.updateDeporte(id, deporte);
        return ResponseEntity.ok(updatedDeporte);
    }

    /**
     * Eliminar un deporte (DELETE /deportes/{id})
     */
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Deporte eliminado correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Deporte no encontrado")
    })
    public ResponseEntity<Void> deleteDeporte(@PathVariable Long id) {
        deporteService.deleteDeporte(id);
        return ResponseEntity.noContent().build();
    }

}
