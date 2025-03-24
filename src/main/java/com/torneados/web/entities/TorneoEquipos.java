package com.torneados.web.entities;

import com.torneados.web.entities.ids.TorneoEquiposId;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TorneoEquipos {

    @EmbeddedId
    private TorneoEquiposId id; // Clave primaria compuesta: torneo + equipo

    private int partidosGanados = 0;
    private int partidosPerdidos = 0;
    private int partidosEmpatados = 0;
    private int golesFavor = 0;
    private int golesContra = 0;
}

