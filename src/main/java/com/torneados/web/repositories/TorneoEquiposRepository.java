package com.torneados.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.TorneoEquipos;
import com.torneados.web.entities.ids.TorneoEquiposId;

public interface TorneoEquiposRepository extends JpaRepository<TorneoEquipos, TorneoEquiposId> {

    List<TorneoEquipos> findByIdTorneoIdTorneo(Long idTorneo);

    //Equipos en un torneo que no han sido eliminados
    List<TorneoEquipos> findByIdTorneoIdTorneoAndEliminadoFalse(Long idTorneo);

}

