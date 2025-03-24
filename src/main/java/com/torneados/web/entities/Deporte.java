package com.torneados.web.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Deporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDeporte;
    
    @Column(unique = true, nullable = false)
    private String deporte;
}

