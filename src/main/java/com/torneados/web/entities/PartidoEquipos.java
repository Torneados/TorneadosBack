package com.torneados.web.entities;

import com.torneados.web.entities.ids.PartidoEquiposId;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PartidoEquipos {

    @EmbeddedId
    private PartidoEquiposId id; // Clave primaria compuesta (incluye partido, equipo y numSet)

    private int puntos;
}
