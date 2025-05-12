package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Torneo;
import com.torneados.web.entities.Usuario;

import java.util.List;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    List<Torneo> findByCreador(Usuario creador);
    
    List<Torneo> findByNombreContainingIgnoreCase(String nombre);
    
    List<Torneo> findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCase(String nombre, String lugar);
    
    List<Torneo> findByNombreContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(String nombre, String deporte);
    
    List<Torneo> findByNombreContainingIgnoreCaseAndLugarContainingIgnoreCaseAndDeporte_DeporteContainingIgnoreCase(String nombre, String lugar, String deporte);
}
