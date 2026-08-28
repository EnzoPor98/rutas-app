package com.rutas.repository;

import com.rutas.models.Ciudad;

public class CiudadRepository extends GenericDAO<Ciudad, Long> {
    public CiudadRepository() {
        super(Ciudad.class);
    }
}
