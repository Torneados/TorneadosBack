package com.torneados.web.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Tipo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipo;
    
    @Column(unique = true, nullable = false)
    private String tipo;
}

