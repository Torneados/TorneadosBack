package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
}
