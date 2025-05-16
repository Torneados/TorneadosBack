package com.torneados.web.entities;

import com.torneados.web.entities.ids.PartidoEquiposId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
public class PartidoEquipos {

    @EmbeddedId
    private PartidoEquiposId id;

    @MapsId("partido")
    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Partido partido;

    @MapsId("equipo")
    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipo equipo;

    private int puntos;
    private boolean esLocal;
}
