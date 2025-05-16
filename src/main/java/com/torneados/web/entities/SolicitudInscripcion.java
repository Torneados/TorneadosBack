package com.torneados.web.entities;

import java.time.LocalDateTime;


import com.torneados.web.entities.ids.SolicitudInscripcionId;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
@Table(name = "solicitud_inscripcion")
public class SolicitudInscripcion {

    @EmbeddedId
    private SolicitudInscripcionId id;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;

    @NotNull(message = "La fecha de solicitud es obligatoria")
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    public enum Estado {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA
    }
}
