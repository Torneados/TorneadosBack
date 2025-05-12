package com.torneados.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.PartidoEquipos;
import com.torneados.web.entities.ids.PartidoEquiposId;

public interface PartidoEquiposRepository extends JpaRepository<PartidoEquipos, PartidoEquiposId> {
    List<PartidoEquipos> findByIdPartidoIdPartido(Long idPartido);


}
