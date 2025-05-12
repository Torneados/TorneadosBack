package com.torneados.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.torneados.web.entities.Tipo;
import com.torneados.web.service.TipoService;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipos")
public class TipoController {
    
    private final TipoService tipoService;

    public TipoController(TipoService tipoService) {
        this.tipoService = tipoService;
    }

    /* 
     * Crea un nuevo tipo (solo el admin puede)
    */
    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created: Tipo creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o el tipo ya existe"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos")
    })
    public ResponseEntity<Tipo> createTipo(@RequestBody Tipo tipo) {
        Tipo nuevoTipo = tipoService.createTipo(tipo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoTipo.getIdTipo())
                .toUri();
        return ResponseEntity.created(location).body(nuevoTipo);
    }
    

    /*
     * Obtener todos los tipos (GET /tipos)
     */
    @GetMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Tipos obtenidos correctamente")
    })
    public ResponseEntity<List<Tipo>> getAllTipos() {
        List<Tipo> tipos = tipoService.getAllTipos();
        return ResponseEntity.ok(tipos);
    }

    /**
     * Actualizar un tipo (PUT /tipos/{id})
     */
    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Tipo actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o el tipo ya existe"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Tipo no encontrado")
    })
    public ResponseEntity<Tipo> updateTipo(@PathVariable Long id, @RequestBody Tipo tipo) {
        Tipo tipoActualizado = tipoService.updateTipo(id, tipo);
        return ResponseEntity.ok(tipoActualizado);
    }

    /**
     * Eliminar un tipo (DELETE /tipos/{id})
     */
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content: Tipo eliminado correctamente"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Falta de permisos"),
        @ApiResponse(responseCode = "404", description = "Not Found: Tipo no encontrado")
    })
    public ResponseEntity<Void> deleteTipo(@PathVariable Long id) {
        tipoService.deleteTipo(id);
        return ResponseEntity.noContent().build();
    }
    
}
