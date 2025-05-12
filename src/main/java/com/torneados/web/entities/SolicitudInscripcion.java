package com.torneados.web.entities;

import java.time.LocalDateTime;

import com.torneados.web.entities.ids.SolicitudInscripcionId;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "solicitud_inscripcion")
public class SolicitudInscripcion {

    @EmbeddedId
    private SolicitudInscripcionId id = new SolicitudInscripcionId();

    @ManyToOne
    @MapsId("idTorneo")
    @JoinColumn(name = "id_torneo")
    @NotNull(message = "El torneo es obligatorio")
    private Torneo torneo;

    @ManyToOne
    @MapsId("idEquipo")
    @JoinColumn(name = "id_equipo")
    @NotNull(message = "El equipo es obligatorio")
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

