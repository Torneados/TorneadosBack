package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.PartidoJugadores;
import com.torneados.web.entities.ids.PartidoJugadoresId;

public interface PartidoJugadoresRepository extends JpaRepository<PartidoJugadores, PartidoJugadoresId> {
}
