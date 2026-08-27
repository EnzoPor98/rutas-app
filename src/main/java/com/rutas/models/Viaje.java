package com.rutas.models;

import java.time.LocalDateTime;
import java.util.List;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "viaje_chofer", joinColumns = @JoinColumn(name = "viaje_id"), inverseJoinColumns = @JoinColumn(name = "chofer_id"))
    private List<Chofer> choferes = new ArrayList<>();

    @OneToMany(mappedBy = "viaje", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pasaje> pasajes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoViaje estado = EstadoViaje.PROGRAMADO;
}
