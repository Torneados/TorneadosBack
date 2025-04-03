package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Jugador;
import java.util.Optional;
import java.util.List;


public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    Optional<Jugador> findByDni(String dni);
    
    boolean existsByDni(String dni);

    List<Jugador> findByEquipoIdEquipo(Long idEquipo);

}
