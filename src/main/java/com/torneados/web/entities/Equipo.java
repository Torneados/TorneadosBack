package com.torneados.web.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Equipo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipo;
    
    @ManyToOne
    @JoinColumn(name = "id_creador")
    private Usuario creador;
    
    private String nombre;
}

