package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Usuario;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    
    // Obtiene todos los equipos creados por un usuario específico.
    List<Equipo> findByCreador(Usuario creador);
    
}
