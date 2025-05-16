package com.torneados.web.entities.ids;

import java.io.Serializable;


import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Torneo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@NoArgsConstructor
@Embeddable
public class SolicitudInscripcionId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_torneo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Torneo torneo;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipo equipo;
}
