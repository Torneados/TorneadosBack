package com.torneados.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.Usuario;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    // Si necesitas también paginar por creador, puedes añadir este método:
    Page<Torneo> findByCreador(Usuario creador, Pageable pageable);

    List<Torneo> findAllByCreador(Usuario creador);

    // Búsqueda paginada sólo por nombre
    Page<Torneo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    
    // Búsqueda paginada por nombre + lugar
    Page<Torneo> findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCase(
        String nombre, String lugar, Pageable pageable);
    
    // Búsqueda paginada por nombre + deporte
    Page<Torneo> findByNombreContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(
        String nombre, String deporte, Pageable pageable);
    
    // Búsqueda paginada por nombre + lugar + deporte
    Page<Torneo> findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(
        String nombre, String lugar, String deporte, Pageable pageable);
}
