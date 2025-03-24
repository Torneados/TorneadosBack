package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Torneo;
import java.util.List;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    // Consulta para buscar torneos por su estado
    List<Torneo> findByEstado(Torneo.EstadoTorneo estado);
    
    // Consulta para buscar torneos por nombre (contiene, ignorando mayúsculas/minúsculas)
    List<Torneo> findByNombreContainingIgnoreCase(String nombre);
}
