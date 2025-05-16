package com.torneados.web.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Entity
@Data
@NoArgsConstructor
public class Partido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPartido;
    
    @ManyToOne
    @JoinColumn(name = "id_torneo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Torneo torneo;
    
    private LocalDateTime fechaComienzo;

    private int ronda;
}
