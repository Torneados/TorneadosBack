package com.torneados.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.torneados.web.entities.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Permite buscar un usuario por su email
    Optional<Usuario> findByEmail(String email);

    // Permite buscar un usuario por su Google ID
    Optional<Usuario> findByGoogleId(String googleId);
}
