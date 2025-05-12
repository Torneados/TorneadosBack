package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Deporte;

public interface DeporteRepository extends JpaRepository<Deporte, Long> {
    boolean existsByDeporte(String deporte);
}

