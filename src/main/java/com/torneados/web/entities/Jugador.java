package com.torneados.web.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Jugador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJugador;
    
    private String nombre;
    
    @Column(unique = true)
    private String dni;
    
    private LocalDate fechaNacimiento;
    
}
