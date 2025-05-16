package com.torneados.web.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Entity
@Data
@NoArgsConstructor
public class Equipo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipo;
    
    @ManyToOne
    @JoinColumn(name = "id_creador", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario creador;
    
    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    private String logoUrl;
}
