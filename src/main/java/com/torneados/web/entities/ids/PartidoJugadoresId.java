package com.torneados.web.entities.ids;

import java.io.Serializable;

import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.Partido;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class PartidoJugadoresId implements Serializable {
    
    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false)
    private Partido partido;

    @ManyToOne
    @JoinColumn(name = "id_jugador", nullable = false)
    private Jugador jugador;

    private int numSet; // Ahora forma parte de la clave primaria
}
