package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.TorneoEquipos;
import com.torneados.web.entities.ids.TorneoEquiposId;

public interface TorneoEquiposRepository extends JpaRepository<TorneoEquipos, TorneoEquiposId> {
}

