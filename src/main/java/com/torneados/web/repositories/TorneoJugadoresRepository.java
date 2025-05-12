package com.torneados.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.torneados.web.entities.TorneoJugadores;
import com.torneados.web.entities.ids.TorneoJugadoresId;

public interface TorneoJugadoresRepository extends JpaRepository<TorneoJugadores, TorneoJugadoresId> {
    
    List<TorneoJugadores> findByIdTorneoIdTorneo(Long idTorneo);

    
}
