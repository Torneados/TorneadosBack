package com.torneados.web.entities.ids;

import java.io.Serializable;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Torneo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class TorneoEquiposId implements Serializable {
    
    @ManyToOne
    @JoinColumn(name = "id_torneo", nullable = false)
    private Torneo torneo;
    
    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;
}
