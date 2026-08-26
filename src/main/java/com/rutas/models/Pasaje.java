package com.rutas.models;

import java.time.LocalDateTime;

import com.rutas.models.Enums.EstadoPasaje;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pasajes")
@Getter
@Setter
public class Pasaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    // Se reemplaza la relación por atributos simples
    @Column(nullable = false, length = 150)
    private String nombreCliente;

    @Column(nullable = false, length = 10)
    private String dniCliente;

    @Column(nullable = false)
    private Integer numeroAsiento;

    @Column(nullable = false)
    private LocalDateTime fechaHoraEmision = LocalDateTime.now();

    @Column(nullable = false)
    private Double precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPasaje estado = EstadoPasaje.RESERVADO;
}
