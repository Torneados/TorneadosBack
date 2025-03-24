package com.torneados.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private String id;      // ID único de Google (sub)
    private String nombre;  // Nombre del usuario
    private String email;   // Correo electrónico
    private String foto;    // URL de la foto de perfil
}
