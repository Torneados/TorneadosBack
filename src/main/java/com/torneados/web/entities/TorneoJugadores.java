package com.torneados.web.entities;


import com.torneados.web.entities.ids.TorneoJugadoresId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
public class TorneoJugadores {

    @EmbeddedId
    private TorneoJugadoresId id;  // compuesto por torneo + jugador

    @MapsId("torneo")
    @ManyToOne
    @JoinColumn(name = "id_torneo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Torneo torneo;

    @MapsId("jugador")
    @ManyToOne
    @JoinColumn(name = "id_jugador", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Jugador jugador;

    private int partidos = 0;
    private int puntos = 0;
    private int tarjetasAmarillas = 0;
    private int tarjetasRojas = 0;
}
