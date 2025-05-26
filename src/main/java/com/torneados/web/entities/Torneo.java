package com.torneados.web.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.URL;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTorneo;

    @NotBlank(message = "El nombre del torneo es obligatorio")
    private String nombre;

    private boolean esPublico;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "El lugar del torneo es obligatorio")
    private String lugar;

    @NotNull(message = "El deporte es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_deporte", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Deporte deporte;

    /** fase de liga (todos contra todos) */
    private boolean liga;

    /** si liga==true, ¿se juega ida y vuelta? */
    private boolean idaYVuelta;

    /** fase de grupos */
    private boolean grupos;

    /** fase KO/eliminatoria */
    private boolean eliminatoria;

    /**
     * Estado actual de la fase del torneo:
     * 0 = inscripción / sin sortear
     * 1 = grupos o liga sorteada
     * 2 = eliminatoria sorteada
     */
    @Column(nullable = false)
    private int fase = 0;

    @ManyToOne
    @JoinColumn(name = "id_creador")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario creador;

    @NotNull(message = "La fecha de comienzo es obligatoria")
    private LocalDateTime fechaComienzo;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @Email(message = "El email debe tener un formato válido")
    private String contactoEmail;

    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    @Column(nullable = true)
    private String contactoTelefono;

    @URL(message = "El enlace de Instagram no es una URL válida")
    @Column(nullable = true)
    private String enlaceInstagram;

    @URL(message = "El enlace de Facebook no es una URL válida")
    @Column(nullable = true)
    private String enlaceFacebook;

    @URL(message = "El enlace de Twitter no es una URL válida")
    @Column(nullable = true)
    private String enlaceTwitter;
}
