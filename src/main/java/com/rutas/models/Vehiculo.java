package com.rutas.models;

import java.time.LocalDateTime;

import com.rutas.models.Enums.EstadoVehiculo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehiculos")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String patente;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(name = "anio_fabricacion")
    private Integer anio;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private LocalDateTime fechaCompra = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;
}
