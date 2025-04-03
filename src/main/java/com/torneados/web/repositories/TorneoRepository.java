package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Torneo;
import java.util.List;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    // Consulta para buscar torneos por su estado
    List<Torneo> findByEstado(Torneo.EstadoTorneo estado);
    
    List<Torneo> findByNombreContainingIgnoreCase(String nombre);
    
    List<Torneo> findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCase(String nombre, String lugar);
    
    List<Torneo> findByNombreContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(String nombre, String deporte);
    
    List<Torneo> findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(String nombre, String lugar, String deporte);
}
