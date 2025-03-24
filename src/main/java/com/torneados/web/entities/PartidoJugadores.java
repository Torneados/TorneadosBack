package com.torneados.web.entities;

import com.torneados.web.entities.ids.PartidoJugadoresId;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PartidoJugadores {

    @EmbeddedId
    private PartidoJugadoresId id; // Clave primaria compuesta

    private boolean jugado;
    private int juegos;
    private int puntos;
}

