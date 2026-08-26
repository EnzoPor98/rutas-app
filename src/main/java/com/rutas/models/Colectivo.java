package com.rutas.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "colectivos")
@PrimaryKeyJoinColumn(name = "vehiculo_id")
@Getter
@Setter
public class Colectivo {
    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private Boolean aireAcondicionado;

    @Column(nullable = false)
    private Boolean wifi;
}
