package com.torneados.web.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Entity
@Data
@NoArgsConstructor
public class Jugador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJugador;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipo equipo;
    
    @NotBlank(message = "El nombre del jugador es obligatorio")
    private String nombre;
    
    private String dni;

    private LocalDate fechaNacimiento;
}
