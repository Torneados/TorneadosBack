package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.torneados.web.entities.EquipoJugadores;
import com.torneados.web.entities.ids.EquipoJugadoresId;

public interface EquipoJugadoresRepository extends JpaRepository<EquipoJugadores, EquipoJugadoresId> {
   
    List<EquipoJugadores> findById_Equipo_IdEquipo(Long idEquipo);


}
