package com.torneados.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Usuario;
import com.torneados.web.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Crea un nuevo usuario (solo para administradores).
     *
     * @param usuario Objeto con los datos del usuario a crear.
     * @return Usuario creado con código 201 y cabecera Location.
     */
    @Operation(summary = "Crear un nuevo usuario (solo admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para crear usuario", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario createdUsuario = usuarioService.createUsuario(usuario);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUsuario.getIdUsuario())
                .toUri();
        return ResponseEntity.created(location).body(createdUsuario);
    }



    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Usuario encontrado. Devuelve 401 si no hay usuario autenticado, 403 si el usuario no tiene permisos y 404 si no se encuentra.
     */
    @Operation(summary = "Obtener un usuario por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para ver este usuario", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Obtiene la lista de equipos de un usuario.
     *
     * GET /usuarios/{id_usuario}/equipos
     *
     * @param idUsuario El ID del usuario.
     * @return La lista de equipos del usuario.
     */
    @Operation(summary = "Obtener los equipos de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK: Equipos obtenidos correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para acceder a los equipos de otros usuarios", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id_usuario}/equipos")
    public ResponseEntity<List<Equipo>> getEquiposByUsuario(@PathVariable("id_usuario") Long idUsuario) {
        List<Equipo> equipos = usuarioService.getEquiposByUsuario(idUsuario);
        return ResponseEntity.ok(equipos);
    }

    /**
     * Actualiza un usuario. 
     * - El propio usuario o un administrador pueden actualizar el nombre y la foto.
     * - Solo el administrador puede modificar el rol.
     *
     * @param usuario Objeto con los datos actualizados.
     * @return Usuario actualizado.
     */
    @Operation(summary = "Actualizar un usuario (solo admin o el propio usuario, rol solo modificable por admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para modificar este usuario", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Usuario no encontrado", content = @Content)
    })
    @PutMapping
    public ResponseEntity<Usuario> updateUsuario(@RequestBody Usuario usuario) {
        Usuario updatedUsuario = usuarioService.updateUsuario(usuario);
        return ResponseEntity.ok(updatedUsuario);
    }


    // UsuarioController.java
    /**
     * Elimina un usuario por su ID.
     * Solo el administrador o el mismo usuario pueden eliminar.
     *
     * @param id ID del usuario a eliminar.
     * @return Respuesta 204 (No Content) en caso de eliminación correcta.
     */
    @Operation(summary = "Eliminar un usuario por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Falta de autenticación", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden: Sin permisos para eliminar este usuario", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found: Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
