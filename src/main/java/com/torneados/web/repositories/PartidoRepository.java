package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Partido;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

    List<Partido> findByTorneoIdTorneo(Long idTorneo);
}
