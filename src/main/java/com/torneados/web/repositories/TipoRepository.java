package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Tipo;

public interface TipoRepository extends JpaRepository<Tipo, Long> {
    boolean existsByTipo(String tipo);
}

