package com.torneados.web.entities;


import com.torneados.web.entities.ids.TorneoEquiposId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
public class TorneoEquipos {

    @EmbeddedId
    private TorneoEquiposId id;  // compuesto por torneo + equipo

    @MapsId("torneo")
    @ManyToOne
    @JoinColumn(name = "id_torneo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Torneo torneo;

    @MapsId("equipo")
    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipo equipo;

    private int partidosGanados = 0;
    private int partidosPerdidos = 0;
    private int partidosEmpatados = 0;
    private int golesFavor = 0;
    private int golesContra = 0;

    private boolean eliminado = false;
    private String grupo = null;
}
