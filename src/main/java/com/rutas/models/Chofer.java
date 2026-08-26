package com.rutas.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "choferes")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
public class Chofer extends Persona {
    @Column(nullable = false, unique = true, length = 20)
    private String numeroLicencia;

    @Column(nullable = false)
    private LocalDate vencimientoLicencia;

    @Column(nullable = false)
    private Boolean activo;
}
