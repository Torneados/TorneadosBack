package com.torneados.web.entities.ids;

import java.io.Serializable;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Jugador;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class EquipoJugadoresId implements Serializable {
    
    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    @ManyToOne
    @JoinColumn(name = "id_jugador", nullable = false)
    private Jugador jugador;
}
