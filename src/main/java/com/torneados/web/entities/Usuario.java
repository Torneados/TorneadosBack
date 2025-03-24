package com.torneados.web.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String googleId;

    @Column(unique = true)
    private String email;

    private String nombre;

    private String foto;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public enum Rol {
        USUARIO, ADMINISTRADOR
    }
}


