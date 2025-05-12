package com.torneados.web.entities;

import com.torneados.web.entities.ids.TorneoJugadoresId;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TorneoJugadores {
     @EmbeddedId
    private TorneoJugadoresId id; // Clave primaria compuesta (incluye torneo y jugador)

    private int partidos = 0;

    private int puntos = 0;

    private int tarjetasAmarillas = 0;

    private int tarjetasRojas = 0;
}
