package com.torneados.web.entities;

import com.torneados.web.entities.ids.EquipoJugadoresId;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class EquipoJugadores {

    @EmbeddedId
    private EquipoJugadoresId id; // Clave compuesta
}
