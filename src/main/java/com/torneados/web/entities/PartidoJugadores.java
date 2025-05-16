package com.torneados.web.entities;


import com.torneados.web.entities.ids.PartidoJugadoresId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
public class PartidoJugadores {

    @EmbeddedId
    private PartidoJugadoresId id;  // compuesto por partido, jugador y numSet

    @MapsId("partido")
    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Partido partido;

    @MapsId("jugador")
    @ManyToOne
    @JoinColumn(name = "id_jugador", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Jugador jugador;

    private boolean jugado = false;
    private int puntos = 0;
    private int tarjetasAmarillas = 0;
    private int tarjetasRojas = 0;
}
