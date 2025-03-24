package com.torneados.web.entities.ids;

import java.io.Serializable;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Partido;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class PartidoEquiposId implements Serializable {
    
    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false)
    private Partido partido;
    
    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;
    
    // Se incluye numSet en la clave para identificar cada set de un partido por equipo
    private int numSet;
}
