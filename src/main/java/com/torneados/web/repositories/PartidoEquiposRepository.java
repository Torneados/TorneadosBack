package com.torneados.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.torneados.web.entities.PartidoEquipos;
import com.torneados.web.entities.ids.PartidoEquiposId;

public interface PartidoEquiposRepository extends JpaRepository<PartidoEquipos, PartidoEquiposId> {

    /**
     * Todos los sets de un partido
     */
    List<PartidoEquipos> findByIdPartidoIdPartido(Long idPartido);

    /**
     * Para cada partido de este torneo en el que jugó idEquipo,
     * devuelve un array [pe, rival], donde:
     *  - pe    = registro de idEquipo,
     *  - rival = registro del otro equipo
     */
    @Query("""
      SELECT pe, r
        FROM PartidoEquipos pe
        JOIN PartidoEquipos r
          ON r.id.partido.idPartido = pe.id.partido.idPartido
         AND r.id.equipo.idEquipo <> pe.id.equipo.idEquipo
       WHERE pe.id.partido.torneo.idTorneo = :idTorneo
         AND pe.id.equipo.idEquipo           = :idEquipo
    """)
    List<Object[]> findPartidosConRival(
      @Param("idTorneo") Long idTorneo,
      @Param("idEquipo") Long idEquipo
    );
}
