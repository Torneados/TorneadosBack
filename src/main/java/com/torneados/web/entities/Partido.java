package com.torneados.web.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Partido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPartido;
    
    @ManyToOne
    @JoinColumn(name = "id_torneo")
    private Torneo torneo;
    
    private LocalDateTime fechaComienzo;
}

