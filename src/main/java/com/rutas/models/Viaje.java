package com.rutas.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.rutas.models.Enums.EstadoViaje;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "viajes")
@Getter
@Setter
public class Viaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "origen_id", nullable = false)
    private Ciudad origen;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "destino_id", nullable = false)
    private Ciudad destino;

    @Column(nullable = false)
    private LocalDateTime fechaHoraSalida;

    @Column(nullable = false)
    private LocalDateTime fechaHoraLlegada;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "chofer_id", nullable = false)
    private ArrayList<Chofer> choferes;

    @OneToMany(mappedBy = "viaje", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArrayList<Pasaje> pasajes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoViaje estado = EstadoViaje.PROGRAMADO;
}
