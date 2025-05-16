package com.torneados.web.entities.ids;

import java.io.Serializable;


import com.torneados.web.entities.Jugador;
import com.torneados.web.entities.Partido;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@NoArgsConstructor
@Embeddable
public class PartidoJugadoresId implements Serializable {
    
    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Partido partido;

    @ManyToOne
    @JoinColumn(name = "id_jugador", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Jugador jugador;

    private int numSet; 
}
