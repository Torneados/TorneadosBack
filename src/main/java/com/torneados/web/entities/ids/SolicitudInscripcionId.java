package com.torneados.web.entities.ids;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudInscripcionId implements Serializable {

    @Column(name = "id_torneo")
    private Long idTorneo;

    @Column(name = "id_equipo")
    private Long idEquipo;
}

