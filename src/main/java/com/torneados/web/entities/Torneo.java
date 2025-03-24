package com.torneados.web.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Torneo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTorneo;
    
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    private String lugar;

    @ManyToOne
    @JoinColumn(name = "id_deporte")
    private Deporte deporte;

    @ManyToOne
    @JoinColumn(name = "id_tipo")
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTorneo estado;

    @ManyToOne
    @JoinColumn(name = "id_creador")
    private Usuario creador;

    private LocalDateTime fechaComienzo;

    private LocalDateTime fechaFin;

    public enum EstadoTorneo {
        PENDIENTE,
        EN_CURSO,
        FINALIZADO
    }
    
}

