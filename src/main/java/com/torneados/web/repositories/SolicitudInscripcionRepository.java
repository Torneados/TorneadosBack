package com.torneados.web.repositories;

import com.torneados.web.entities.SolicitudInscripcion;
import com.torneados.web.entities.ids.SolicitudInscripcionId;

import io.micrometer.common.lang.NonNull;

import com.torneados.web.entities.SolicitudInscripcion.Estado;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.Equipo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudInscripcionRepository extends JpaRepository<SolicitudInscripcion, SolicitudInscripcionId> {

    List<SolicitudInscripcion> findByIdTorneo(Torneo torneo);

    List<SolicitudInscripcion> findByIdTorneoAndEstado(Torneo torneo, Estado estado);

    boolean existsById(@NonNull SolicitudInscripcionId id);

    List<SolicitudInscripcion> findByIdEquipo(Equipo equipo);
}
